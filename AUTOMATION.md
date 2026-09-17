# Дипломный проект — автоматизация и Allure

## 1. Что добавлено в проект

### UI-тесты

Тесты находятся в:

`app/src/androidTest/java/ru/edu/qamid/tests/`

- `TestBase.kt` — общая настройка тестов, запуск Activity, ожидание экранов, авторизация и выход.
- `AuthTest.kt` — авторизация и выход из учётной записи.
- `NavigationTest.kt` — боковое меню, News, тематические цитаты и раскрытие карточки.
- `NewsTest.kt` — список новостей, сортировка, фильтр и панель управления.

Используется Espresso. Для ожидания экранов после сетевых операций используется UiAutomator только как механизм синхронизации; взаимодействия с UI выполняются через Espresso.

В тестах соблюдается требование диплома: действие сопровождается проверкой результата.

## 2. Allure

В `app/build.gradle` добавлены зависимости Allure Kotlin Android 2.4.0 и JUnit4-интеграция.

Тестовый runner:

`io.qameta.allure.android.runners.AllureAndroidJUnitRunner`

Результаты сохраняются в каталоге `allure-results` внутри файлового каталога тестового приложения.

В `app/src/androidTest/resources/allure.properties` задано:

`allure.results.directory=allure-results`

Для скриншотов при падении используется `ScreenshotRule` с режимом `FAILURE`.

## 3. Сохранение APK

В `gradle.properties` добавлен обязательный для диплома параметр:

`android.injected.androidTest.leaveApksInstalledAfterRun=true`

Он предотвращает удаление APK приложения после запуска instrumentation-тестов.

## 4. Запуск тестов

Из корня проекта:

```text
./gradlew connectedDebugAndroidTest
```

В Windows:

```text
gradlew.bat connectedDebugAndroidTest
```

Для запуска отдельного класса:

```text
gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=ru.edu.qamid.tests.AuthTest
```

## 5. Выгрузка Allure results с эмулятора

После выполнения тестов определить подключённое устройство:

```text
adb devices
```

Для текущего applicationId `ru.edu.qamid` результаты можно выгрузить из приватного каталога приложения:

```text
adb exec-out run-as ru.edu.qamid sh -c "cd /data/data/ru.edu.qamid/files && tar cf - allure-results" > allure-results.tar
```

После этого распаковать архив в каталог `allure-results`.

## 6. Генерация HTML-отчёта

При установленном Allure Report:

```text
allure generate allure-results -o allure-report --clean
```

Для просмотра:

```text
allure open allure-report
```

Либо:

```text
allure serve allure-results
```

## 7. Что должно попасть в итоговый архив

Рекомендуемая структура:

```text
Diplom/
├── app/
├── gradle/
├── gradlew
├── gradlew.bat
├── build.gradle
├── settings.gradle
├── gradle.properties
├── Plan.md
├── Cases.xlsx
├── Check.xlsx
├── AUTOMATION.md
├── Result.md
└── allure-report/
```

В архив не требуется включать `.gradle`, `app/build` и другие сгенерированные каталоги, если они не нужны для демонстрации.
