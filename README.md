# Дипломный проект: тестирование приложения «Мобильный хоспис»

В проекте реализованы Java UI-тесты с JUnit 4, Espresso, UiAutomator и Allure. Экраны оформлены как Page Objects: они находятся в `app/src/androidTest/java/ru/edu/qamid/tests/pages`, тесты — в `app/src/androidTest/java/ru/edu/qamid/tests/tests`.

## Требования

- JDK 17;
- Android SDK Platform 36 и эмулятор/устройство с API 36;
- доступ к сети и тестовому backend;
- учётная запись `login2` / `password2` с правами администратора для тестов панели новостей.

## Сборка

В PowerShell из корня проекта выполните:

```powershell
.\gradlew.bat assembleDebug assembleDebugAndroidTest
```

## Запуск всех UI-тестов

Запустите эмулятор API 36 или подключите устройство, затем выполните:

```powershell
adb devices -l
.\gradlew.bat connectedDebugAndroidTest
```

Для запуска одного класса укажите его имя. Например:

```powershell
.\gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=ru.edu.qamid.tests.tests.AuthTest
```

Тесты разделены на классы `AuthTest`, `NavigationTest`, `NewsFilterTest`, `NewsTest` и `QuotesTest`. Всего реализовано 34 тестовых метода, сопоставленных с 33 кейсами из `Cases.xlsx`; один метод дополнительно проверяет два пустых поля авторизации. Флаг `android.injected.androidTest.leaveApksInstalledAfterRun=true` в `gradle.properties` оставляет APK установленными после прогона.

## Allure

Перед полным прогоном удалите на подключённом устройстве только каталог старых Allure-результатов и выполните весь набор тестов. Так в архив не попадут результаты предыдущих запусков:

```powershell
adb shell run-as ru.edu.qamid rm -rf /data/data/ru.edu.qamid/files/allure-results
.\gradlew.bat connectedDebugAndroidTest
```

Для запусков через Gradle CLI анимации отключаются настройкой `testOptions.animationsDisabled` в `app/build.gradle`. При запуске тестов напрямую из Android Studio эта настройка не применяется.

После прогона результаты выгружаются с подключённого устройства, затем создаётся архив:

```powershell
.\scripts\pull-allure-results.bat
```

Скрипт сохраняет реальные файлы в `allure-results/` и создаёт `allure-results.zip` в корне проекта. Если на устройстве нет результатов, скрипт завершится ошибкой. Для просмотра HTML-отчёта при установленном Allure CLI выполните:

```powershell
allure generate allure-results -o allure-report --clean
```

## Основные файлы

- `Plan.md` — план и покрытие тест-кейсов;
- `Check.xlsx` — ручные проверки и отдельная колонка автоматизации;
- `Cases.xlsx` — тест-кейсы и Java-методы;
- `Result.md` — результаты и сравнение времени;
- `allure-results.zip` — архив результатов реального прогона.
