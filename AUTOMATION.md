# Автоматизация UI-тестов

## Стек и устройство тестов

Тесты написаны на Java 17 с JUnit 4 и AndroidX Test. Для UI используются Espresso, для popup-меню, системных диалогов и ожидания элементов — UiAutomator. Экраны оформлены как Page Objects; сценарии распределены по классам в соответствии с функциями приложения.

Для отчётов подключён Allure Kotlin Android 2.4.0: добавлены `@Epic`, `@Feature`, `@DisplayName`, `@Description`, шаги и скриншоты при падении. В `TestBase` настроены запуск Activity, сброс сохранённой авторизации и начальный экран. Тесты не используют `Thread.sleep()` и не зависят от порядка запуска.

## Классы тестов

- `AuthTest` — успешный вход, неверные данные и пустые поля;
- `NavigationTest` — News через меню и ALL NEWS, тематические цитаты и выход;
- `NewsFilterTest` — сортировка и фильтры списка новостей;
- `NewsTest` — панель управления, создание, редактирование, удаление, статусы и обязательные поля;
- `QuotesTest` — раскрытие и сворачивание цитаты.

Реализовано 34 метода `@Test`, сопоставленных с 33 кейсами в `Cases.xlsx`.

## Запуск

```powershell
.\gradlew.bat assembleDebug assembleDebugAndroidTest
.\gradlew.bat connectedDebugAndroidTest
```

Для запуска нужны Android API 36, сеть, доступный backend и учётная запись `login2` / `password2`. Для операций панели управления требуются права администратора.

## Allure

После тестового прогона результаты выгружаются и архивируются командой:

```powershell
.\scripts\pull-allure-results.bat
```

Скрипт сохраняет реальные результаты в `allure-results/` и `allure-results.zip`. Для HTML-отчёта при установленном Allure CLI выполните:

```powershell
allure generate allure-results -o allure-report --clean
```
