$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$allureDirectory = Join-Path $projectRoot 'allure-results'
$xmlDirectory = Join-Path $projectRoot 'app\build\outputs\androidTest-results\connected\debug'
$xmlReport = Get-ChildItem -LiteralPath $xmlDirectory -Filter 'TEST-*.xml' -File |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if ($null -eq $xmlReport) {
    throw "No Gradle XML report found in $xmlDirectory"
}

[xml]$testReport = Get-Content -LiteralPath $xmlReport.FullName -Raw
$suite = $testReport.testsuite
$expectedCases = @{}
foreach ($case in $suite.testcase) {
    $fullName = "$($case.classname).$($case.name)"
    $status = 'passed'
    if ($null -ne $case.failure -or $null -ne $case.error) {
        $status = 'failed'
    } elseif ($null -ne $case.skipped) {
        $status = 'skipped'
    }
    $expectedCases[$fullName] = $status
}

$reportTime = [DateTimeOffset]::Parse(
    [string]$suite.timestamp,
    [Globalization.CultureInfo]::InvariantCulture,
    [Globalization.DateTimeStyles]::AssumeUniversal
).ToUnixTimeMilliseconds()
$suiteDuration = [double]$suite.time * 1000
$tolerance = 120000
$windowStart = $reportTime - $suiteDuration - $tolerance
$windowEnd = $reportTime + $tolerance

$candidates = @()
foreach ($file in Get-ChildItem -LiteralPath $allureDirectory -Filter '*-result.json' -File) {
    $result = Get-Content -LiteralPath $file.FullName -Raw | ConvertFrom-Json
    if ($null -eq $result.fullName -or [long]$result.stop -le 0) {
        continue
    }
    if ([long]$result.start -ge $windowStart -and [long]$result.stop -le $windowEnd) {
        $candidates += [pscustomobject]@{
            File = $file
            Result = $result
        }
    }
}

$selected = @{}
foreach ($candidate in $candidates) {
    $fullName = [string]$candidate.Result.fullName
    if (-not $expectedCases.ContainsKey($fullName)) {
        continue
    }
    if (-not $selected.ContainsKey($fullName) -or
        [long]$candidate.Result.stop -gt [long]$selected[$fullName].Result.stop) {
        $selected[$fullName] = $candidate
    }
}

$missingCases = @($expectedCases.Keys | Where-Object { -not $selected.ContainsKey($_) })
if ($missingCases.Count -gt 0) {
    throw "Allure is missing $($missingCases.Count) results from the latest run: $($missingCases -join ', ')"
}
if ($selected.Count -ne $expectedCases.Count) {
    throw "Allure result count ($($selected.Count)) does not match Gradle test count ($($expectedCases.Count))."
}

foreach ($fullName in $expectedCases.Keys) {
    $actualStatus = [string]$selected[$fullName].Result.status
    if ($actualStatus -ne $expectedCases[$fullName]) {
        throw "Status mismatch for $($fullName): Gradle=$($expectedCases[$fullName]), Allure=$actualStatus"
    }
}

$stagingDirectory = Join-Path $env:TEMP ('allure-current-' + [guid]::NewGuid().ToString('N'))
$stagingResults = Join-Path $stagingDirectory 'allure-results'
$stagingArchive = Join-Path $projectRoot ('.allure-results-' + [guid]::NewGuid().ToString('N') + '.zip')
New-Item -ItemType Directory -Path $stagingResults -Force | Out-Null

function Copy-AllureAttachments($node, $sourceDirectory, $destinationDirectory) {
    if ($null -eq $node) { return }
    foreach ($attachment in @($node.attachments)) {
        if ($null -ne $attachment.source) {
            $sourcePath = Join-Path $sourceDirectory ([string]$attachment.source)
            if (-not (Test-Path -LiteralPath $sourcePath -PathType Leaf)) {
                throw "Allure attachment is missing: $($attachment.source)"
            }
            Copy-Item -LiteralPath $sourcePath -Destination $destinationDirectory -Force
        }
    }
    foreach ($step in @($node.steps)) {
        Copy-AllureAttachments $step $sourceDirectory $destinationDirectory
    }
    foreach ($fixture in @($node.befores)) {
        Copy-AllureAttachments $fixture $sourceDirectory $destinationDirectory
    }
    foreach ($fixture in @($node.afters)) {
        Copy-AllureAttachments $fixture $sourceDirectory $destinationDirectory
    }
}

try {
    foreach ($candidate in $selected.Values) {
        Copy-Item -LiteralPath $candidate.File.FullName -Destination $stagingResults -Force
        Copy-AllureAttachments $candidate.Result $allureDirectory $stagingResults
    }

    Compress-Archive -Path (Join-Path $stagingResults '*') -DestinationPath $stagingArchive -Force
    Move-Item -LiteralPath $stagingArchive -Destination (Join-Path $projectRoot 'allure-results.zip') -Force
    Write-Output "Validated and archived $($selected.Count) Allure results from $($xmlReport.Name)."
} finally {
    if (Test-Path -LiteralPath $stagingArchive) {
        Remove-Item -LiteralPath $stagingArchive -Force
    }
    if (Test-Path -LiteralPath $stagingDirectory) {
        Remove-Item -LiteralPath $stagingDirectory -Recurse -Force
    }
}
