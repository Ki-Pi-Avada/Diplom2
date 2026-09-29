@echo off
setlocal
if exist allure-results.tar del /q allure-results.tar
if not exist allure-results mkdir allure-results
adb exec-out run-as ru.edu.qamid sh -c "cd /data/data/ru.edu.qamid/files && tar cf - allure-results" > allure-results.tar
if errorlevel 1 (
  echo Failed to pull Allure results from the connected device.
  exit /b 1
)
tar -tf allure-results.tar | findstr /i ".json" >nul
if errorlevel 1 (
  echo No Allure JSON results were found on the device.
  del /q allure-results.tar
  exit /b 1
)
tar -xf allure-results.tar -C .
if errorlevel 1 (
  echo Failed to extract Allure results.
  exit /b 1
)
del /q allure-results.tar
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\validate-allure-results.ps1
if errorlevel 1 (
  echo Allure results do not match the latest Gradle test run. Existing archive was kept.
  exit /b 1
)
echo Allure results extracted and archived in allure-results.zip.
