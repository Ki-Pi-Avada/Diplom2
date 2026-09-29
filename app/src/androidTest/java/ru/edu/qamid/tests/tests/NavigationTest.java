package ru.edu.qamid.tests.tests;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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
import ru.edu.qamid.tests.pages.NewsListPage;

@RunWith(AndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Навигация")
public class NavigationTest extends TestBase {
    private LoginPage loginPage;
    private MainPage mainPage;

    @Before
    public void signIn() {
        loginPage = new LoginPage();
        mainPage = new MainPage();
        loginPage.loginIfNeeded("login2", "password2");
        mainPage.checkMainScreen();
    }

    @Test
    @DisplayName("Раздел тематических цитат открывается")
    @Description("Проверяет переход в раздел цитат и отображение его заголовка")
    public void openThematicQuotes() {
        mainPage.openOurMission();
        mainPage.checkOurMissionTitle();
    }

    @Test
    @DisplayName("ALL NEWS открывает полный список новостей")
    @Description("Проверяет переход к списку новостей из блока новостей на главном экране")
    public void openAllNews() {
        mainPage.openAllNews();
    }

    @Test
    @DisplayName("Раздел новостей открывается из бокового меню")
    @Description("Проверяет переход к списку новостей через пункт бокового меню")
    public void openNewsFromMenu() {
        mainPage.openNewsFromMenu();
        new NewsListPage(device).checkNewsScreenDisplayed();
    }

    @Test
    @DisplayName("Пользователь может выйти из учётной записи")
    @Description("Проверяет завершение сессии и возврат к форме авторизации")
    public void logOut() {
        mainPage.logOut();
        loginPage.checkLoginScreen();
    }
}
