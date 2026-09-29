package ru.edu.qamid.tests.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import io.qameta.allure.kotlin.Allure;
import ru.edu.qamid.R;

public class FilterNewsPage {
    private final UiDevice device;
    private final boolean controlPanelFilter;

    public FilterNewsPage(UiDevice device, boolean controlPanelFilter) {
        this.device = device;
        this.controlPanelFilter = controlPanelFilter;
    }

    public void checkFilterScreen() {
        Allure.step("Проверить открытие формы фильтрации новостей");
        onView(withId(R.id.filter_news_title_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_news_category_auto_complete)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_news_apply_button)).check(matches(isDisplayed()));
    }

    public void selectCategory(String category) {
        Allure.step("Выбрать категорию фильтра: " + category);
        onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon), isDisplayed()))
                .perform(click());
        onData(equalTo(category)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.filter_news_category_auto_complete)).check(matches(withText(category)));
    }

    public void selectOnlyActive() {
        Allure.step("Оставить в фильтре только активные новости");
        onView(withId(R.id.filter_news_active_check_box)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_news_inactive_check_box)).perform(click());
    }

    public void selectOnlyInactive() {
        Allure.step("Оставить в фильтре только неактивные новости");
        onView(withId(R.id.filter_news_inactive_check_box)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_news_active_check_box)).perform(click());
    }

    public void apply() {
        Allure.step("Применить фильтр новостей");
        onView(withId(R.id.filter_news_apply_button)).perform(click());
        String expectedScreen = controlPanelFilter
                ? "ru.edu.qamid:id/news_control_panel_app_bar"
                : "ru.edu.qamid:id/news_list_app_bar";
        assertTrue("После применения фильтра исходный экран не открылся",
                device.wait(Until.hasObject(By.res(expectedScreen)), 10_000));
    }
}
