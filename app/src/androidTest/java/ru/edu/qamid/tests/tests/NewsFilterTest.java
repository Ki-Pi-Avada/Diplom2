package ru.edu.qamid.tests.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Epic;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.junit4.DisplayName;
import ru.edu.qamid.R;
import ru.edu.qamid.tests.TestBase;
import ru.edu.qamid.tests.pages.FilterNewsPage;
import ru.edu.qamid.tests.pages.LoginPage;
import ru.edu.qamid.tests.pages.MainPage;
import ru.edu.qamid.tests.pages.NewsControlPanelPage;
import ru.edu.qamid.tests.pages.NewsListPage;

@RunWith(AndroidJUnit4.class)
@Epic("Мобильный хоспис")
@Feature("Список и фильтрация новостей")
public class NewsFilterTest extends TestBase {
    private MainPage mainPage;
    private NewsListPage newsListPage;
    private NewsControlPanelPage newsControlPanelPage;

    @Before
    public void signInAndOpenNews() {
        LoginPage loginPage = new LoginPage();
        mainPage = new MainPage();
        loginPage.loginIfNeeded("login2", "password2");
        mainPage.checkMainScreen();
        newsListPage = new NewsListPage(device);
        newsControlPanelPage = new NewsControlPanelPage(device);
        mainPage.openNewsFromMenu();
    }

    @Test
    @DisplayName("Список новостей сортируется по новизне")
    @Description("Проверяет, что переключение сортировки меняет порядок существующих новостей на обратный")
    public void sortNewsByPublishDate() {
        String[] currentOrder = newsListPage.getFirstTwoNewsTitles();

        newsListPage.toggleSortOrder();
        newsListPage.assertNewsOrder(currentOrder[1], currentOrder[0]);
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Объявление")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByAnnouncement() {
        verifyCategoryFilter("Объявление");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории День рождения")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByBirthday() {
        verifyCategoryFilter("День рождения");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Зарплата")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterBySalary() {
        verifyCategoryFilter("Зарплата");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Профсоюз")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByUnion() {
        verifyCategoryFilter("Профсоюз");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Праздник")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByHoliday() {
        verifyCategoryFilter("Праздник");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Массаж")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByMassage() {
        verifyCategoryFilter("Массаж");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Благодарность")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByGratitude() {
        verifyCategoryFilter("Благодарность");
    }

    @Test
    @DisplayName("Фильтр показывает новости категории Нужна помощь")
    @Description("Проверяет категорию новости в результате фильтрации")
    public void filterByHelp() {
        verifyCategoryFilter("Нужна помощь");
    }

    @Test
    @DisplayName("Фильтр без категории оставляет полный список новостей")
    @Description("Проверяет применение фильтра, когда категория не выбрана")
    public void applyFilterWithoutCategory() {
        String title = "UI unfiltered " + System.currentTimeMillis();
        seedNews("Объявление", title, "Unfiltered fixture", Calendar.getInstance());

        newsListPage.openFilter();
        FilterNewsPage filterPage = new FilterNewsPage(device, false);
        filterPage.checkFilterScreen();
        filterPage.apply();

        newsListPage.assertNewsVisible(title);
    }

    @Test
    @DisplayName("Сброс фильтра возвращает новости разных категорий")
    @Description("Проверяет очистку категории после применения фильтра")
    public void resetFilter() {
        String announcement = "UI reset announcement " + System.currentTimeMillis();
        String holiday = "UI reset holiday " + System.currentTimeMillis();
        seedNews("Объявление", announcement, "Announcement reset fixture", Calendar.getInstance());
        seedNews("Праздник", holiday, "Holiday reset fixture", Calendar.getInstance());

        newsListPage.assertNewsVisible(holiday);
        newsListPage.openFilter();
        FilterNewsPage filterPage = new FilterNewsPage(device, false);
        filterPage.selectCategory("Объявление");
        filterPage.apply();
        newsListPage.assertNewsVisible(announcement);
        newsListPage.assertNewsAbsent(holiday);

        newsListPage.openFilter();
        filterPage.apply();
        newsListPage.assertNewsVisible(announcement);
        newsListPage.assertNewsVisible(holiday);
    }

    private void verifyCategoryFilter(String category) {
        String expectedTitle = "UI category " + category + " " + System.currentTimeMillis();
        String otherCategory = "Объявление".equals(category) ? "Праздник" : "Объявление";
        String otherTitle = "UI category control " + System.currentTimeMillis();
        seedNews(otherCategory, otherTitle, "Control category fixture", Calendar.getInstance());
        seedNews(category, expectedTitle, "Expected category fixture", Calendar.getInstance());

        newsListPage.assertNewsVisible(otherTitle);
        newsListPage.openFilter();
        FilterNewsPage filterPage = new FilterNewsPage(device, false);
        filterPage.checkFilterScreen();
        filterPage.selectCategory(category);
        filterPage.apply();

        newsListPage.assertNewsVisible(expectedTitle);
        newsListPage.assertNewsAbsent(otherTitle);
        newsListPage.openNewsCard(expectedTitle);
        newsListPage.assertNewsDetails(expectedTitle, "Expected category fixture");
    }

    private void seedNews(String category, String title, String description, Calendar publishAt) {
        newsListPage.openControlPanel();
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNewsAtCategoryAndTime(category, title, description, publishAt);
        newsControlPanelPage.assertNewsVisible(title);
        newsListPage.returnToNewsFromControlPanel();
    }
}
