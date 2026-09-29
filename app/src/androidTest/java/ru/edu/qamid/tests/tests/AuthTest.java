package ru.edu.qamid.tests.tests;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Epic;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.junit4.DisplayName;
import ru.edu.qamid.tests.TestBase;
import ru.edu.qamid.tests.pages.LoginPage;
import ru.edu.qamid.tests.pages.MainPage;

@RunWith(AndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Авторизация")
public class AuthTest extends TestBase {
    private LoginPage loginPage;
    private MainPage mainPage;

    @Before
    public void initPages() {
        loginPage = new LoginPage();
        mainPage = new MainPage();
        if (device.hasObject(By.res("ru.edu.qamid:id/main_app_bar"))) {
            mainPage.logOut();
        }
        loginPage.checkLoginScreen();
    }

    @Test
    @DisplayName("Пользователь входит с корректными данными")
    @Description("Проверяет вход тестового пользователя и открытие главного экрана")
    public void successfulLogin() {
        loginPage.checkLoginScreen();
        loginPage.enterLogin("login2");
        loginPage.enterPassword("password2");
        loginPage.clickSignInButton();
        mainPage.checkMainScreen();
    }

    @Test
    @DisplayName("Неверные данные не авторизуют пользователя")
    @Description("Проверяет отказ во входе с незарегистрированными учётными данными")
    public void loginWithUnregisteredUser() {
        loginPage.checkLoginScreen();
        loginPage.enterLogin("Login123");
        loginPage.enterPassword("password123");
        loginPage.clickSignInButton();
        loginPage.waitForSignInRequestToFinish();
        loginPage.checkFormStillOpen();
    }

    @Test
    @DisplayName("Пустые логин и пароль не принимаются")
    @Description("Проверяет обязательность обоих полей формы авторизации")
    public void loginWithEmptyFields() {
        loginPage.checkLoginScreen();
        loginPage.enterLogin("");
        loginPage.enterPassword("");
        loginPage.clickSignInButton();
        loginPage.checkFormStillOpen();
    }

    @Test
    @DisplayName("Пустой логин не принимается")
    @Description("Проверяет обязательность поля логина")
    public void loginWithEmptyLogin() {
        loginPage.checkLoginScreen();
        loginPage.enterLogin("");
        loginPage.enterPassword("password2");
        loginPage.clickSignInButton();
        loginPage.checkFormStillOpen();
    }

    @Test
    @DisplayName("Пустой пароль не принимается")
    @Description("Проверяет обязательность поля пароля")
    public void loginWithEmptyPassword() {
        loginPage.checkLoginScreen();
        loginPage.enterLogin("login2");
        loginPage.enterPassword("");
        loginPage.clickSignInButton();
        loginPage.checkFormStillOpen();
    }
}
