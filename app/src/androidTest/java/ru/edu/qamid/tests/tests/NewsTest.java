package ru.edu.qamid.tests.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
@Feature("Новости")
public class NewsTest extends TestBase {
    private LoginPage loginPage;
    private MainPage mainPage;
    private NewsListPage newsListPage;
    private NewsControlPanelPage newsControlPanelPage;

    @Before
    public void signInAndOpenControlPanel() {
        loginPage = new LoginPage();
        mainPage = new MainPage();
        loginPage.loginIfNeeded("login2", "password2");
        mainPage.checkMainScreen();
        newsListPage = new NewsListPage(device);
        newsControlPanelPage = new NewsControlPanelPage(device);
        mainPage.openNewsFromMenu();
        newsListPage.openControlPanel();
    }

    @Test
    @DisplayName("Администратор создаёт новость")
    @Description("Проверяет заполнение формы и появление новой новости в панели управления")
    public void createNews() {
        String title = "UI news " + System.currentTimeMillis();
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNews(title, "UI test description");
        newsControlPanelPage.assertNewsVisible(title);
    }

    @Test
    @DisplayName("Администратор редактирует новость")
    @Description("Создаёт отдельную тестовую новость, меняет её заголовок и проверяет результат")
    public void editNews() {
        String originalTitle = "UI edit " + System.currentTimeMillis();
        String updatedTitle = originalTitle + " updated";
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNews(originalTitle, "UI edit test description");
        newsControlPanelPage.assertNewsVisible(originalTitle);
        newsControlPanelPage.editNews(originalTitle, updatedTitle);
    }

    @Test
    @DisplayName("Панель управления сортирует новости по дате публикации")
    @Description("Проверяет, что переключение сортировки меняет порядок существующих новостей на обратный")
    public void sortNewsInControlPanel() {
        String[] currentOrder = newsControlPanelPage.getFirstTwoNewsTitles();

        onView(withId(R.id.news_sort_button)).perform(click());
        newsControlPanelPage.assertNewsOrder(currentOrder[1], currentOrder[0]);
    }

    @Test
    @DisplayName("Карточка новости в панели управления раскрывает описание")
    @Description("Раскрывает первую доступную карточку и проверяет непустое отображаемое описание")
    public void viewNewsInControlPanel() {
        newsControlPanelPage.openFirstNewsCard();
        newsControlPanelPage.assertAnyNewsDescriptionVisible();
    }

    @Test
    @DisplayName("Администратор удаляет новость")
    @Description("Проверяет удаление созданной тестовой новости из панели управления")
    public void deleteNews() {
        String title = "UI delete " + System.currentTimeMillis();
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNews(title, "News to delete");
        newsControlPanelPage.assertNewsVisible(title);
        newsControlPanelPage.deleteNews(title);
    }

    @Test
    @DisplayName("Администратор меняет статус новости на неактивный")
    @Description("Проверяет сохранение выключенного статуса публикации новости")
    public void changeNewsStatusToInactive() {
        String title = "UI inactive " + System.currentTimeMillis();
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNews(title, "News status test");
        newsControlPanelPage.assertNewsVisible(title);
        newsControlPanelPage.setNewsActive(title, false);
    }

    @Test
    @DisplayName("Фильтр панели управления показывает активные новости")
    @Description("Проверяет отбор активной новости и исключение неактивной")
    public void filterActiveNews() {
        String activeTitle = "UI active filter " + System.currentTimeMillis();
        String inactiveTitle = "UI inactive filter " + System.currentTimeMillis();
        createActiveAndInactiveNews(activeTitle, inactiveTitle);

        onView(withId(R.id.news_filter_button)).perform(click());
        FilterNewsPage filterPage = new FilterNewsPage(device, true);
        filterPage.checkFilterScreen();
        filterPage.selectOnlyActive();
        filterPage.apply();

        newsControlPanelPage.assertNewsVisible(activeTitle);
        newsControlPanelPage.assertNewsAbsent(inactiveTitle);
    }

    @Test
    @DisplayName("Фильтр панели управления показывает неактивные новости")
    @Description("Проверяет отбор неактивной новости и исключение активной")
    public void filterInactiveNews() {
        String activeTitle = "UI active filter " + System.currentTimeMillis();
        String inactiveTitle = "UI inactive filter " + System.currentTimeMillis();
        createActiveAndInactiveNews(activeTitle, inactiveTitle);

        onView(withId(R.id.news_filter_button)).perform(click());
        FilterNewsPage filterPage = new FilterNewsPage(device, true);
        filterPage.checkFilterScreen();
        filterPage.selectOnlyInactive();
        filterPage.apply();

        newsControlPanelPage.assertNewsVisible(inactiveTitle);
        newsControlPanelPage.assertNewsAbsent(activeTitle);
    }

    @Test
    @DisplayName("Создание новости без категории не сохраняет её")
    @Description("Проверяет обязательность категории при создании новости")
    public void createNewsWithoutCategory() {
        assertInvalidNewsForm(false, true, true, true, true);
    }

    @Test
    @DisplayName("Создание новости без даты публикации не сохраняет её")
    @Description("Проверяет обязательность даты публикации")
    public void createNewsWithoutDate() {
        assertInvalidNewsForm(true, true, false, true, true);
    }

    @Test
    @DisplayName("Создание новости без времени не сохраняет её")
    @Description("Проверяет обязательность времени публикации")
    public void createNewsWithoutTime() {
        assertInvalidNewsForm(true, true, true, false, true);
    }

    @Test
    @DisplayName("Создание новости без описания не сохраняет её")
    @Description("Проверяет обязательность описания новости")
    public void createNewsWithoutDescription() {
        assertInvalidNewsForm(true, true, true, true, false);
    }

    @Test
    @DisplayName("Создание новости без заголовка не сохраняет её")
    @Description("Проверяет обязательность заголовка новости")
    public void createNewsWithoutTitle() {
        assertInvalidNewsForm(true, false, true, true, true);
    }

    private void createActiveAndInactiveNews(String activeTitle, String inactiveTitle) {
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNews(activeTitle, "Active filter fixture");
        newsControlPanelPage.assertNewsVisible(activeTitle);
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.createNews(inactiveTitle, "Inactive filter fixture");
        newsControlPanelPage.assertNewsVisible(inactiveTitle);
        newsControlPanelPage.setNewsActive(inactiveTitle, false);
    }

    private void assertInvalidNewsForm(
            boolean includeCategory,
            boolean includeTitle,
            boolean includeDate,
            boolean includeTime,
            boolean includeDescription
    ) {
        newsControlPanelPage.openCreateForm();
        newsControlPanelPage.fillNewsForm(
                "Объявление",
                "Invalid UI news " + System.currentTimeMillis(),
                "Required field test",
                includeCategory,
                includeTitle,
                includeDate,
                includeTime,
                includeDescription
        );
        newsControlPanelPage.saveNewsForm();
        newsControlPanelPage.assertCreateFormOpen();
    }
}
