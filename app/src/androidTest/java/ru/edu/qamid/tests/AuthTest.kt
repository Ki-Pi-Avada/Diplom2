package ru.edu.qamid.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.By
import org.junit.Before
import org.junit.Test
import ru.edu.qamid.R

class AuthTest : TestBase() {


    @Before
    fun prepareAuthorizationScreen() {
        ensureLoggedOut()
    }

    @Test
    fun successfulLogin() {

        step("Ввести зарегистрированный логин") {
            onView(withId(R.id.login_edit_text))
                .perform(replaceText("login2"))
                .check(matches(isDisplayed()))
        }

        step("Ввести зарегистрированный пароль") {
            onView(withId(R.id.password_edit_text))
                .perform(replaceText("password2"))
                .check(matches(isDisplayed()))
        }

        step("Подождать после ввода данных") {
            pauseForUi(2000)
        }

        step("Нажать кнопку Sign in") {
            onView(withId(R.id.enter_button))
                .perform(click())
        }

        step("Проверить открытие главного экрана") {
            waitForObject("main_app_bar")

            onView(withId(R.id.main_app_bar))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun logout() {

        step("Авторизоваться зарегистрированным пользователем") {
            login()
        }

        step("Проверить открытие главного экрана") {
            waitForObject("main_app_bar")

            onView(withId(R.id.main_app_bar))
                .check(matches(isDisplayed()))
        }

        step("Открыть меню профиля") {
            onView(withId(R.id.authorization_image_button))
                .perform(click())
        }

        step("Проверить появление пункта Log out") {
            onView(withText("Log out"))
                .check(matches(isDisplayed()))
        }

        step("Выполнить выход из учётной записи") {
            onView(withText("Log out"))
                .perform(click())
        }

        step("Проверить возврат на экран Authorization") {
            waitForObject("login_edit_text")

            onView(withId(R.id.login_edit_text))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun loginWithEmptyFields() {

        step("Проверить, что поля авторизации пустые") {
            onView(withId(R.id.login_edit_text))
                .check(matches(isDisplayed()))

            onView(withId(R.id.password_edit_text))
                .check(matches(isDisplayed()))
        }

        step("Нажать кнопку Sign in") {
            onView(withId(R.id.enter_button))
                .perform(click())
                .check(matches(isDisplayed()))
        }

        step("Проверить, что авторизация не выполнена") {

            pauseForUi(1000)

            check(
                device.hasObject(
                    By.res("ru.edu.qamid:id/login_edit_text")
                )
            ) {
                "После отправки пустых данных приложение покинуло экран Authorization"
            }

            onView(withId(R.id.login_edit_text))
                .check(matches(isDisplayed()))

            onView(withId(R.id.password_edit_text))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun loginWithEmptyLogin() {

        step("Ввести пароль") {
            onView(withId(R.id.password_edit_text))
                .perform(replaceText("password2"))
                .check(matches(isDisplayed()))
        }

        step("Подождать после ввода данных") {
            pauseForUi(1000)
        }

        step("Нажать кнопку Sign in") {
            onView(withId(R.id.enter_button))
                .perform(click())
                .check(matches(isDisplayed()))
        }

        step("Проверить, что авторизация не выполнена") {

            pauseForUi(1000)

            check(
                device.hasObject(
                    By.res("ru.edu.qamid:id/login_edit_text")
                )
            ) {
                "После отправки данных приложение покинуло экран Authorization"
            }

            onView(withId(R.id.login_edit_text))
                .check(matches(isDisplayed()))

            onView(withId(R.id.password_edit_text))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun loginWithEmptyPassword() {

        step("Ввести логин") {
            onView(withId(R.id.login_edit_text))
                .perform(replaceText("login2"))
                .check(matches(isDisplayed()))
        }

        step("Подождать после ввода данных") {
            pauseForUi(1000)
        }

        step("Нажать кнопку Sign in") {
            onView(withId(R.id.enter_button))
                .perform(click())
                .check(matches(isDisplayed()))
        }

        step("Проверить, что авторизация не выполнена") {

            pauseForUi(1000)

            check(
                device.hasObject(
                    By.res("ru.edu.qamid:id/login_edit_text")
                )
            ) {
                "После отправки данных приложение покинуло экран Authorization"
            }

            onView(withId(R.id.login_edit_text))
                .check(matches(isDisplayed()))

            onView(withId(R.id.password_edit_text))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun loginWithUnregisteredUser() {

        step("Ввести незарегистрированный логин") {
            onView(withId(R.id.login_edit_text))
                .perform(replaceText("Login123"))
                .check(matches(isDisplayed()))
        }

        step("Ввести пароль") {
            onView(withId(R.id.password_edit_text))
                .perform(replaceText("password123"))
                .check(matches(isDisplayed()))
        }

        step("Подождать после ввода данных") {
            pauseForUi(1000)
        }

        step("Отправить данные авторизации") {
            onView(withId(R.id.enter_button))
                .perform(click())
                .check(matches(isDisplayed()))
        }

        step("Проверить, что авторизация не выполнена") {

            pauseForUi(1000)

            check(
                device.hasObject(
                    By.res("ru.edu.qamid:id/login_edit_text")
                )
            ) {
                "Незарегистрированный пользователь получил доступ к Main"
            }

            onView(withId(R.id.login_edit_text))
                .check(matches(isDisplayed()))

            onView(withId(R.id.password_edit_text))
                .check(matches(isDisplayed()))
        }
    }
}