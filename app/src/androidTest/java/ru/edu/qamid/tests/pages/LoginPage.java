package ru.edu.qamid.tests.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import io.qameta.allure.kotlin.Allure;
import ru.edu.qamid.R;

public class LoginPage {
    private final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    public void checkLoginScreen() {
        Allure.step("Проверить отображение формы авторизации");
        device.wait(Until.hasObject(By.res("ru.edu.qamid:id/auth_app_bar")), 15_000);
        onView(withId(R.id.auth_app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.login_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.password_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_button)).check(matches(isDisplayed()));
    }

    public void enterLogin(String login) {
        Allure.step("Ввести логин");
        onView(withId(R.id.login_edit_text)).perform(click(), replaceText(login), closeSoftKeyboard());
        onView(withId(R.id.login_edit_text)).check(matches(isDisplayed()));
    }

    public void enterPassword(String password) {
        Allure.step("Ввести пароль");
        onView(withId(R.id.password_edit_text)).perform(click(), replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).check(matches(isDisplayed()));
    }

    public void clickSignInButton() {
        Allure.step("Нажать кнопку «Войти»");
        onView(withId(R.id.enter_button)).check(matches(isDisplayed())).perform(click());
    }

    public void login(String login, String password) {
        enterLogin(login);
        enterPassword(password);
        clickSignInButton();
    }

    public void loginIfNeeded(String login, String password) {
        if (device.hasObject(By.res("ru.edu.qamid:id/main_app_bar"))) {
            return;
        }
        if (!device.hasObject(By.res("ru.edu.qamid:id/auth_app_bar"))) {
            throw new AssertionError("Не отображается главный экран или форма авторизации");
        }
        login(login, password);
    }

    public void waitForSignInRequestToFinish() {
        Allure.step("Дождаться завершения запроса авторизации");
        boolean buttonEnabled = device.wait(
                Until.hasObject(By.res("ru.edu.qamid:id/enter_button").enabled(true)),
                20_000
        );
        if (!buttonEnabled) {
            throw new AssertionError("Запрос авторизации не завершился за 20 секунд");
        }
    }

    public void checkFormStillOpen() {
        Allure.step("Проверить, что пользователь остался на форме авторизации");
        onView(withId(R.id.auth_app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.login_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.password_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_button)).check(matches(isDisplayed()));
    }
}
