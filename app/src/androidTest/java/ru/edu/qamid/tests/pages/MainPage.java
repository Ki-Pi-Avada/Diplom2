package ru.edu.qamid.tests.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;
import androidx.test.uiautomator.UiObject2;

import io.qameta.allure.kotlin.Allure;
import ru.edu.qamid.R;

public class MainPage {
    private final UiDevice device;

    public MainPage() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public void waitForMainScreen() {
        boolean screenOpened = device.wait(
                Until.hasObject(By.res("ru.edu.qamid:id/main_app_bar")),
                30_000
        );
        assertTrue("Главный экран не открылся после авторизации", screenOpened);
    }

    public boolean isMainScreen() {
        return device.wait(Until.hasObject(By.res("ru.edu.qamid:id/main_app_bar")), 10_000);
    }

    public void checkMainScreen() {
        Allure.step("Проверить отображение главного экрана");
        waitForMainScreen();
        onView(withId(R.id.main_app_bar)).check(matches(isDisplayed()));
    }

    public void openMenu() {
        Allure.step("Открыть главное меню");
        onView(withId(R.id.main_menu_image_button)).check(matches(isDisplayed())).perform(click());
    }

    public void openNewsFromMenu() {
        Allure.step("Открыть новости из главного меню");
        openMenu();
        clickPopupItem(R.string.news);
        if (!device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_list_app_bar")), 10_000)) {
            throw new AssertionError("Экран новостей не открылся");
        }
        onView(withId(R.id.news_list_app_bar)).check(matches(isDisplayed()));
    }

    public void openAllNews() {
        Allure.step("Открыть полный список новостей через ALL NEWS");
        onView(withId(R.id.all_news_text_view)).check(matches(isDisplayed())).perform(click());
        if (!device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_list_app_bar")), 10_000)) {
            throw new AssertionError("Переход ALL NEWS не открыл список новостей");
        }
        onView(withId(R.id.news_list_app_bar)).check(matches(isDisplayed()));
    }

    public void openAuthorization() {
        Allure.step("Открыть меню авторизации");
        onView(withId(R.id.authorization_image_button)).check(matches(isDisplayed())).perform(click());
    }

    public void logOut() {
        Allure.step("Выйти из учётной записи");
        openAuthorization();
        clickPopupItem(R.string.log_out);
        if (!device.wait(Until.hasObject(By.res("ru.edu.qamid:id/auth_app_bar")), 10_000)) {
            throw new AssertionError("После выхода не открылась форма авторизации");
        }
        onView(withId(R.id.auth_app_bar)).check(matches(isDisplayed()));
    }

    public void openOurMission() {
        Allure.step("Открыть раздел тематических цитат");
        onView(withId(R.id.our_mission_image_button)).check(matches(isDisplayed())).perform(click());
        if (!device.wait(Until.hasObject(By.res("ru.edu.qamid:id/our_mission_title_text_view")), 10_000)) {
            throw new AssertionError("Раздел тематических цитат не открылся");
        }
        onView(withId(R.id.our_mission_title_text_view)).check(matches(isDisplayed()));
    }

    public void checkOurMissionTitle() {
        String expectedTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getString(R.string.our_mission_title_text);
        onView(withId(R.id.our_mission_title_text_view)).check(matches(withText(expectedTitle)));
    }

    private void clickPopupItem(int titleResourceId) {
        String title = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getString(titleResourceId);
        UiObject2 menuItem = device.wait(Until.findObject(By.text(title)), 5_000);
        if (menuItem == null) {
            throw new AssertionError("Не найден пункт меню: " + title);
        }
        menuItem.click();
    }
}
