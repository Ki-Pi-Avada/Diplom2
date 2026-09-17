package ru.edu.qamid.tests

import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import io.qameta.allure.android.rules.ScreenshotRule
import io.qameta.allure.kotlin.Allure
import org.junit.Rule
import ru.edu.qamid.R
import ru.edu.qamid.ui.AppActivity

abstract class TestBase {

    protected val device: UiDevice =
        UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation()
        )

    @get:Rule
    val activityRule =
        ActivityScenarioRule(AppActivity::class.java)

    @get:Rule
    val screenshotRule = ScreenshotRule(
        mode = ScreenshotRule.Mode.FAILURE,
        screenshotName = "screenshot-on-failure"
    )

    /**
     * Приводит приложение к экрану Authorization.
     *
     * Метод вызывается только в тех тестах,
     * которым действительно нужен экран Authorization.
     */
    protected fun ensureLoggedOut(timeout: Long = 20_000) {

        val loginField =
            By.res("ru.edu.qamid:id/login_edit_text")

        val mainAppBar =
            By.res("ru.edu.qamid:id/main_app_bar")

        val authorizationButton =
            By.res("ru.edu.qamid:id/authorization_image_button")

        val logoutItem =
            By.text("Log out")

        val startTime = SystemClock.uptimeMillis()

        while (SystemClock.uptimeMillis() - startTime < timeout) {

            // Уже открыт Authorization.
            if (device.hasObject(loginField)) {

                onView(withId(R.id.login_edit_text))
                    .check(matches(isDisplayed()))

                return
            }

            // Открыт Main — выполняем logout.
            if (device.hasObject(mainAppBar)) {

                onView(withId(R.id.main_app_bar))
                    .check(matches(isDisplayed()))

                if (!device.hasObject(logoutItem)) {

                    check(
                        device.wait(
                            Until.hasObject(authorizationButton),
                            5_000
                        )
                    ) {
                        "Не найдена кнопка меню профиля"
                    }

                    onView(withId(R.id.authorization_image_button))
                        .perform(click())
                }

                check(
                    device.wait(
                        Until.hasObject(logoutItem),
                        5_000
                    )
                ) {
                    "Не появился пункт Log out"
                }

                onView(withText("Log out"))
                    .check(matches(isDisplayed()))
                    .perform(click())

                waitForLogin()
                return
            }

            SystemClock.sleep(300)
        }

        throw IllegalStateException(
            "Не удалось привести приложение к экрану Authorization"
        )
    }

    /**
     * Приводит приложение к состоянию авторизованного пользователя.
     *
     * Если Main уже открыт — ничего не делает.
     * Если Authorization — выполняет login().
     */
    protected fun ensureLoggedIn(timeout: Long = 20_000) {

        val mainAppBar =
            By.res("ru.edu.qamid:id/main_app_bar")

        val loginField =
            By.res("ru.edu.qamid:id/login_edit_text")

        val startTime = SystemClock.uptimeMillis()

        while (SystemClock.uptimeMillis() - startTime < timeout) {

            if (device.hasObject(mainAppBar)) {

                onView(withId(R.id.main_app_bar))
                    .check(matches(isDisplayed()))

                return
            }

            if (device.hasObject(loginField)) {

                onView(withId(R.id.login_edit_text))
                    .check(matches(isDisplayed()))

                login()
                return
            }

            SystemClock.sleep(300)
        }

        throw IllegalStateException(
            "Не удалось дождаться экрана Authorization или Main"
        )
    }

    /**
     * Ожидание экрана Authorization.
     */
    protected fun waitForLogin(timeout: Long = 20_000) {

        val loginField =
            By.res("ru.edu.qamid:id/login_edit_text")

        check(
            device.wait(
                Until.hasObject(loginField),
                timeout
            )
        ) {
            "Не удалось дождаться экрана Authorization"
        }

        onView(withId(R.id.login_edit_text))
            .check(matches(isDisplayed()))
    }

    /**
     * Авторизация зарегистрированным пользователем.
     */
    protected fun login(timeout: Long = 20_000) {

        val mainAppBar =
            By.res("ru.edu.qamid:id/main_app_bar")

        val loginField =
            By.res("ru.edu.qamid:id/login_edit_text")

        val passwordField =
            By.res("ru.edu.qamid:id/password_edit_text")

        val enterButton =
            By.res("ru.edu.qamid:id/enter_button")

        onView(withId(R.id.login_edit_text))
            .perform(replaceText("login2"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.password_edit_text))
            .perform(replaceText("password2"))
            .check(matches(isDisplayed()))

        var loginAttempt = 0
        val maxAttempts = 2

        while (loginAttempt < maxAttempts) {

            loginAttempt++

            step("Нажать кнопку входа. Попытка $loginAttempt") {

                check(
                    device.wait(
                        Until.hasObject(enterButton),
                        5_000
                    )
                ) {
                    "Кнопка входа не найдена"
                }

                onView(withId(R.id.enter_button))
                    .check(matches(isDisplayed()))
                    .perform(click())
            }

            val loginStartedAt = SystemClock.uptimeMillis()

            while (SystemClock.uptimeMillis() - loginStartedAt < timeout) {

                if (device.hasObject(mainAppBar)) {

                    onView(withId(R.id.main_app_bar))
                        .check(matches(isDisplayed()))

                    return
                }

                SystemClock.sleep(300)
            }

            // Main не открылся.
            // Если это была первая попытка, проверяем,
            // что экран авторизации всё ещё открыт,
            // и пробуем войти ещё раз.
            if (loginAttempt < maxAttempts) {

                step("Повторить авторизацию") {

                    check(
                        device.wait(
                            Until.hasObject(loginField),
                            5_000
                        )
                    ) {
                        "После первой попытки не удалось вернуться к экрану Authorization"
                    }

                    onView(withId(R.id.login_edit_text))
                        .check(matches(isDisplayed()))

                    onView(withId(R.id.password_edit_text))
                        .check(matches(isDisplayed()))

                    onView(withId(R.id.login_edit_text))
                        .perform(replaceText("login2"))

                    onView(withId(R.id.password_edit_text))
                        .perform(replaceText("password2"))

                    pauseForUi(500)
                }
            }
        }

        throw IllegalStateException(
            "Не удалось выполнить авторизацию после $maxAttempts попыток"
        )
    }

    /**
     * Allure step.
     */
    protected fun step(
        name: String,
        action: () -> Unit
    ) {
        Allure.step(name) {
            action()
        }
    }

    /**
     * Ожидание элемента по resource ID.
     */
    protected fun waitForObject(
        resourceId: String,
        timeout: Long = 15_000
    ) {

        check(
            device.wait(
                Until.hasObject(
                    By.res("ru.edu.qamid:id/$resourceId")
                ),
                timeout
            )
        ) {
            "Не найден элемент: $resourceId"
        }
    }

    /**
     * Ожидание фактической загрузки новостей.
     */
    protected fun waitForNews(
        timeout: Long = 30_000
    ) {

        val newsTitle =
            By.res("ru.edu.qamid:id/news_item_title_text_view")

        check(
            device.wait(
                Until.hasObject(newsTitle),
                timeout
            )
        ) {
            "Новости не загрузились из базы данных за $timeout мс"
        }

        check(
            device.hasObject(newsTitle)
        ) {
            "После ожидания список новостей остался пустым"
        }
    }

    protected fun pauseForUi(
        milliseconds: Long = 300
    ) {
        SystemClock.sleep(milliseconds)
    }
}