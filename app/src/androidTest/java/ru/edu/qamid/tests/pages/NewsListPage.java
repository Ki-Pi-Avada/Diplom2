package ru.edu.qamid.tests.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import io.qameta.allure.kotlin.Allure;
import org.hamcrest.Matcher;
import ru.edu.qamid.R;

public class NewsListPage {
    private final UiDevice device;

    public NewsListPage(UiDevice device) {
        this.device = device;
    }

    public void openFromMainMenu() {
        Allure.step("Открыть раздел новостей из главного меню");
        onView(withId(R.id.main_menu_image_button)).perform(click());
        String menuTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getString(R.string.news);
        UiObject2 menuItem = device.wait(Until.findObject(By.text(menuTitle)), 5_000);
        if (menuItem == null) {
            throw new AssertionError("Не найден пункт меню: " + menuTitle);
        }
        menuItem.click();
        if (!device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_list_app_bar")), 10_000)) {
            throw new AssertionError("Экран новостей не открылся");
        }
        onView(withId(R.id.news_list_app_bar)).check(matches(isDisplayed()));
    }

    public void checkNewsScreenDisplayed() {
        Allure.step("Проверить отображение списка новостей");
        onView(withId(R.id.news_list_app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.news_list_recycler_view)).check(matches(isDisplayed()));
    }

    public void openControlPanel() {
        Allure.step("Открыть панель управления новостями");
        onView(withId(R.id.news_edit_button)).check(matches(isDisplayed())).perform(click());
        assertTrue("Панель управления новостями не открылась",
                device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_control_panel_app_bar")), 10_000));
        onView(withId(R.id.news_control_panel_app_bar)).check(matches(isDisplayed()));
    }

    public void returnToNewsFromControlPanel() {
        Allure.step("Вернуться к списку новостей из панели управления");
        onView(withId(R.id.main_menu_image_button)).perform(click());
        String menuTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getString(R.string.news);
        UiObject2 menuItem = device.wait(Until.findObject(By.text(menuTitle)), 5_000);
        if (menuItem == null) {
            throw new AssertionError("Не найден пункт меню: " + menuTitle);
        }
        menuItem.click();
        assertTrue("Не вернулись к списку новостей",
                device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_list_app_bar")), 10_000));
    }

    public void openFilter() {
        Allure.step("Открыть фильтр новостей");
        onView(withId(R.id.news_filter_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.filter_news_title_text_view)).check(matches(isDisplayed()));
    }

    public void toggleSortOrder() {
        Allure.step("Переключить сортировку списка новостей");
        onView(withId(R.id.news_sort_button)).check(matches(isDisplayed())).perform(click());
    }

    public void assertNewsVisible(String title) {
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.scrollTo(hasDescendant(withText(title)))
        );
        onView(allOf(withId(R.id.news_item_title_text_view), withText(title)))
                .check(matches(isDisplayed()));
    }

    public void assertNewsAbsent(String title) {
        assertTrue("Новость не исчезла из отфильтрованного списка: " + title,
                device.wait(Until.gone(
                        By.res("ru.edu.qamid:id/news_item_title_text_view").text(title)
                ), 10_000));
    }

    public void openNewsCard(String title) {
        Allure.step("Раскрыть карточку новости: " + title);
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(title)),
                        click()
                )
        );
    }

    public void assertNewsDetails(String title, String description) {
        onView(withId(R.id.news_list_recycler_view)).check(matches(hasDescendant(allOf(
                withId(R.id.news_item_material_card_view),
                hasDescendant(withText(title)),
                hasDescendant(withText(description))
        ))));
    }

    public void assertNewsOrder(String titleExpectedFirst, String titleExpectedSecond) {
        int firstPosition = findNewsPosition(titleExpectedFirst);
        int secondPosition = findNewsPosition(titleExpectedSecond);
        assertTrue("Ожидалась новость '" + titleExpectedFirst + "' раньше '"
                + titleExpectedSecond + "', позиции: " + firstPosition + " и " + secondPosition,
                firstPosition < secondPosition);
    }

    public String[] getFirstTwoNewsTitles() {
        String[] titles = new String[2];
        onView(withId(R.id.news_list_recycler_view)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "Получить заголовки первых двух новостей";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                assertTrue("Для проверки сортировки нужны как минимум две новости",
                        recyclerView.getAdapter() != null
                                && recyclerView.getAdapter().getItemCount() >= 2);

                for (int position = 0; position < titles.length; position++) {
                    RecyclerView.ViewHolder holder = recyclerView
                            .findViewHolderForAdapterPosition(position);
                    if (holder == null) {
                        throw new AssertionError("Не отображена новость с позицией " + position);
                    }
                    TextView titleView = holder.itemView
                            .findViewById(R.id.news_item_title_text_view);
                    if (titleView == null) {
                        throw new AssertionError("Не найден заголовок новости с позицией " + position);
                    }
                    titles[position] = titleView.getText().toString();
                }
            }
        });
        return titles;
    }

    private int findNewsPosition(String title) {
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.scrollTo(hasDescendant(withText(title)))
        );
        int[] position = {RecyclerView.NO_POSITION};
        onView(withId(R.id.news_list_recycler_view)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "Получить позицию новости '" + title + "' в списке";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View itemView = recyclerView.getChildAt(i);
                    TextView titleView = itemView.findViewById(R.id.news_item_title_text_view);
                    if (titleView != null && title.equals(titleView.getText().toString())) {
                        position[0] = recyclerView.getChildAdapterPosition(itemView);
                        return;
                    }
                }
            }
        });
        assertTrue("Новость не найдена в списке: " + title,
                position[0] != RecyclerView.NO_POSITION);
        return position[0];
    }
}
