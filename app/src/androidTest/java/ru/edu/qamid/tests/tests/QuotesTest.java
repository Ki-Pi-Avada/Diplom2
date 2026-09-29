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
import ru.edu.qamid.tests.pages.QuotesPage;

@RunWith(AndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Тематические цитаты")
public class QuotesTest extends TestBase {
    private QuotesPage quotesPage;

    @Before
    public void signInAndOpenQuotes() {
        LoginPage loginPage = new LoginPage();
        loginPage.loginIfNeeded("login2", "password2");
        MainPage mainPage = new MainPage();
        mainPage.checkMainScreen();
        mainPage.openOurMission();
        quotesPage = new QuotesPage();
        quotesPage.checkQuotesListDisplayed();
    }

    @Test
    @DisplayName("Цитата раскрывается и сворачивается повторным нажатием")
    @Description("Проверяет отображение описания цитаты и его скрытие при повторном нажатии")
    public void expandAndCollapseQuote() {
        quotesPage.toggleFirstQuote();
        quotesPage.checkFirstQuoteExpanded();
        quotesPage.toggleFirstQuote();
        quotesPage.checkFirstQuoteCollapsed();
    }
}
