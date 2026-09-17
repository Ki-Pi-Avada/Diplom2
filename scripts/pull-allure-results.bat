@echo off
setlocal
if exist allure-results.tar del /q allure-results.tar
adb exec-out run-as ru.edu.qamid sh -c "cd /data/data/ru.edu.qamid/files && tar cf - allure-results" > allure-results.tar
if errorlevel 1 (
  echo Failed to pull Allure results.
  exit /b 1
)
if exist allure-results rmdir /s /q allure-results
mkdir allure-results
powershell -NoProfile -Command "tar -xf allure-results.tar -C ."
echo Allure results copied.
