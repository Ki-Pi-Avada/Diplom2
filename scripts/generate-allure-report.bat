@echo off
allure generate allure-results -o allure-report --clean
if errorlevel 1 exit /b 1
allure open allure-report
