package ru.edu.qamid.tests.pages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import io.qameta.allure.kotlin.Allure;
import ru.edu.qamid.R;

public class QuotesPage {
    public void checkQuotesListDisplayed() {
        Allure.step("Проверить отображение списка тематических цитат");
        onView(withId(R.id.our_mission_item_list_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.our_mission_item_list_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.our_mission_item_list_recycler_view)).check(matches(hasDescendant(
                withId(R.id.our_mission_item_title_text_view)
        )));
    }

    public void toggleFirstQuote() {
        Allure.step("Раскрыть или свернуть первую цитату");
        onView(withId(R.id.our_mission_item_list_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
    }

    public void checkFirstQuoteExpanded() {
        Allure.step("Проверить отображение описания первой цитаты");
        onView(withId(R.id.our_mission_item_list_recycler_view)).check(matches(hasDescendant(allOf(
                withId(R.id.our_mission_item_material_card_view),
                hasDescendant(allOf(
                        withId(R.id.our_mission_item_description_text_view),
                        isDisplayed(),
                        withText(not(""))
                ))
        ))));
    }

    public void checkFirstQuoteCollapsed() {
        Allure.step("Проверить, что описание первой цитаты скрыто");
        onView(withId(R.id.our_mission_item_list_recycler_view)).check(matches(hasDescendant(allOf(
                withId(R.id.our_mission_item_material_card_view),
                hasDescendant(allOf(
                        withId(R.id.our_mission_item_description_text_view),
                        withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                ))
        ))));
    }
}
