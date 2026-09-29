package ru.edu.qamid.tests.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.TimePicker;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.hamcrest.Matchers;
import org.hamcrest.Matcher;

import java.util.Calendar;

import io.qameta.allure.kotlin.Allure;
import ru.edu.qamid.R;

public class NewsControlPanelPage {
    private final UiDevice device;

    public NewsControlPanelPage(UiDevice device) {
        this.device = device;
    }

    public void openCreateForm() {
        Allure.step("Открыть форму создания новости");
        onView(withId(R.id.add_news_image_view)).check(matches(isDisplayed())).perform(click());
        waitForCreateForm();
    }

    public void waitForCreateForm() {
        assertTrue("Форма новости не открылась",
                device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_save_button")), 10_000));
        onView(withId(R.id.news_save_button)).check(matches(isDisplayed()));
    }

    public void createNews(String title, String description) {
        createNews("Объявление", title, description);
    }

    public void createNews(String category, String title, String description) {
        fillNewsForm(category, title, description, true, true, true, true, true);
        saveNewsForm();
        waitForControlPanelAfterSave();
    }

    public void createNewsAt(String title, String description, Calendar publishAt) {
        createNewsAtCategoryAndTime("Объявление", title, description, publishAt);
    }

    public void createNewsAtCategoryAndTime(
            String category,
            String title,
            String description,
            Calendar publishAt
    ) {
        Allure.step("Создать новость с заданными датой и временем публикации");
        selectCategory(category);
        enterTitle(title);
        setDateAndTime(publishAt);
        enterDescription(description);
        saveNewsForm();
        waitForControlPanelAfterSave();
    }

    public void fillNewsForm(
            String category,
            String title,
            String description,
            boolean includeCategory,
            boolean includeTitle,
            boolean includeDate,
            boolean includeTime,
            boolean includeDescription
    ) {
        Allure.step("Заполнить выбранные поля формы новости");
        if (includeCategory) {
            selectCategory(category);
        }
        if (includeTitle) {
            enterTitle(title);
        } else {
            clearTitle();
        }
        if (includeDate) {
            chooseDefaultDate();
        }
        if (includeTime) {
            chooseDefaultTime();
        }
        if (includeDescription) {
            enterDescription(description);
        }
    }

    public void selectCategory(String category) {
        Allure.step("Выбрать категорию новости: " + category);
        onView(allOf(withId(com.google.android.material.R.id.text_input_end_icon), isDisplayed()))
                .perform(click());
        onData(equalTo(category)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.news_category_auto_complete)).check(matches(withText(category)));
    }

    public void enterTitle(String title) {
        onView(withId(R.id.news_title_edit_text))
                .perform(click(), replaceText(title), closeSoftKeyboard());
        onView(withId(R.id.news_title_edit_text)).check(matches(withText(title)));
    }

    public void clearTitle() {
        onView(withId(R.id.news_title_edit_text))
                .perform(click(), replaceText(""), closeSoftKeyboard());
    }

    public void enterDescription(String description) {
        onView(withId(R.id.news_description_edit_text))
                .perform(click(), replaceText(description), closeSoftKeyboard());
        onView(withId(R.id.news_description_edit_text)).check(matches(withText(description)));
    }

    public void chooseDefaultDate() {
        openPicker("news_publish_date_edit_text", "android:id/datePicker");
        confirmDateOrTimePicker();
        onView(withId(R.id.news_publish_date_edit_text))
                .check(matches(withText(not(equalTo("")))));
    }

    public void chooseDefaultTime() {
        openPicker("news_publish_time_edit_text", "android:id/timePicker");
        confirmDateOrTimePicker();
        onView(withId(R.id.news_publish_time_edit_text))
                .check(matches(withText(not(equalTo("")))));
    }

    public void setDateAndTime(Calendar publishAt) {
        openPicker("news_publish_date_edit_text", "android:id/datePicker");
        confirmDateOrTimePicker();

        openPicker("news_publish_time_edit_text", "android:id/timePicker");
        onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(
                publishAt.get(Calendar.HOUR_OF_DAY),
                publishAt.get(Calendar.MINUTE)
        ));
        confirmDateOrTimePicker();
    }

    public void saveNewsForm() {
        Allure.step("Нажать кнопку сохранения новости");
        onView(withId(R.id.news_save_button)).check(matches(isDisplayed())).perform(click());
    }

    private void waitForControlPanelAfterSave() {
        assertTrue("После сохранения не открылась панель управления новостями",
                device.wait(Until.hasObject(
                        By.res("ru.edu.qamid:id/news_control_panel_app_bar")
                ), 30_000));
        onView(withId(R.id.news_control_panel_app_bar)).check(matches(isDisplayed()));
    }

    public void assertCreateFormOpen() {
        Allure.step("Проверить, что форма осталась открыта из-за незаполненного поля");
        assertTrue("Форма закрылась, хотя обязательное поле не заполнено",
                device.wait(Until.hasObject(By.res("ru.edu.qamid:id/news_save_button")), 5_000));
        onView(withId(R.id.news_save_button)).check(matches(isDisplayed()));
    }

    public void assertNewsVisible(String title) {
        Allure.step("Проверить, что новость отображается в панели управления");
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.scrollTo(hasDescendant(withText(title)))
        );
        onView(allOf(withId(R.id.news_item_title_text_view), withText(title)))
                .check(matches(isDisplayed()));
    }

    public void assertNewsAbsent(String title) {
        Allure.step("Проверить отсутствие новости в панели управления");
        assertTrue("Новость всё ещё отображается: " + title,
                device.wait(Until.gone(
                        By.res("ru.edu.qamid:id/news_item_title_text_view").text(title)
                ), 10_000));
    }

    public void editNews(String currentTitle, String updatedTitle) {
        openNewsForEditing(currentTitle);
        enterTitle(updatedTitle);
        saveNewsForm();
        waitForControlPanelAfterSave();
        assertNewsVisible(updatedTitle);
    }

    public void setNewsActive(String title, boolean active) {
        openNewsForEditing(title);
        if (!active) {
            onView(withId(R.id.news_active_switch)).check(matches(isDisplayed())).perform(click());
        }
        saveNewsForm();
        waitForControlPanelAfterSave();
        assertNewsStatus(title, active ? "ACTIVE" : "NOT ACTIVE");
    }

    public void deleteNews(String title) {
        Allure.step("Удалить тестовую новость");
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(title)),
                        RecyclerViewChildActions.clickChildViewWithId(R.id.news_item_delete_image_view)
                )
        );
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        assertNewsAbsent(title);
    }

    public void openNewsCard(String title) {
        Allure.step("Раскрыть карточку новости в панели управления");
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(title)),
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Нажать на карточку новости: " + title;
                            }

                            @Override
                            public void perform(UiController uiController, View itemView) {
                                if (!itemView.performClick()) {
                                    throw new IllegalStateException(
                                            "Обработчик карточки новости не вызван: " + title
                                    );
                                }
                            }
                        }
                )
        );
    }

    public void openFirstNewsCard() {
        Allure.step("Раскрыть первую новость в панели управления");
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isAssignableFrom(View.class);
                    }

                    @Override
                    public String getDescription() {
                        return "Нажать на первую карточку новости";
                    }

                    @Override
                    public void perform(UiController uiController, View itemView) {
                        if (!itemView.performClick()) {
                            throw new IllegalStateException("Не удалось раскрыть карточку новости");
                        }
                    }
                })
        );
    }

    public void assertAnyNewsDescriptionVisible() {
        Allure.step("Проверить, что описание новости отображается после раскрытия карточки");
        UiObject2 description = device.wait(
                Until.findObject(By.res("ru.edu.qamid:id/news_item_description_text_view")),
                5_000
        );
        assertNotNull("Описание раскрытой новости не появилось", description);
        assertTrue("Описание новости не отображается",
                !description.getVisibleBounds().isEmpty());
        assertTrue("У новости пустое описание", !description.getText().trim().isEmpty());
    }

    public void assertNewsDescriptionVisible(String title, String description) {
        assertTrue("Описание новости не раскрылось: " + title,
                device.wait(Until.hasObject(
                        By.res("ru.edu.qamid:id/news_item_description_text_view").text(description)
                ), 5_000));
        onView(withId(R.id.news_list_recycler_view)).check(matches(hasDescendant(allOf(
                withId(R.id.news_item_material_card_view),
                hasDescendant(withText(title)),
                hasDescendant(allOf(withId(R.id.news_item_description_text_view),
                        withText(description), isDisplayed()))
        ))));
    }

    public void assertNewsStatus(String title, String status) {
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.scrollTo(hasDescendant(withText(title)))
        );
        onView(withId(R.id.news_list_recycler_view)).check(matches(hasDescendant(allOf(
                withId(R.id.news_item_material_card_view),
                hasDescendant(withText(title)),
                hasDescendant(withText(status))
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
                return "Получить заголовки первых двух новостей панели управления";
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
                            android.widget.TextView titleView = itemView.findViewById(
                                    R.id.news_item_title_text_view
                            );
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

    private void openNewsForEditing(String title) {
        Allure.step("Открыть редактирование новости: " + title);
        onView(withId(R.id.news_list_recycler_view)).perform(
                RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(title)),
                        RecyclerViewChildActions.clickChildViewWithId(R.id.news_item_edit_image_view)
                )
        );
        waitForCreateForm();
        onView(withId(R.id.news_title_edit_text)).check(matches(isDisplayed()));
    }

    private void confirmDateOrTimePicker() {
        UiObject2 confirmButton = device.wait(
                Until.findObject(By.res("android:id/button1")),
                5_000
        );
        assertNotNull("Кнопка подтверждения даты или времени не найдена", confirmButton);
        confirmButton.click();
    }

    private void openPicker(String fieldId, String pickerId) {
        UiObject2 field = device.wait(
                Until.findObject(By.res("ru.edu.qamid:id/" + fieldId)),
                10_000
        );
        assertNotNull("Поле выбора даты/времени не найдено: " + fieldId, field);
        field.click();
        assertNotNull("Диалог выбора даты/времени не открылся: " + pickerId,
                device.wait(Until.findObject(By.res(pickerId)), 5_000));
    }
}
