package ru.edu.qamid.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Test
import ru.edu.qamid.R

class NewsTest : TestBase() {

    @Test
    fun openNewsFromMenu() {

        step("Авторизоваться или продолжить работу авторизованным") {
            ensureLoggedIn()
        }

        step("Открыть боковое меню") {
            onView(withId(R.id.main_menu_image_button))
                .perform(click())

            onView(withText("News"))
                .check(matches(isDisplayed()))
        }

        step("Выбрать News") {
            onView(withText("News"))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun openNewsFromAllNews() {

        step("Авторизоваться или продолжить работу авторизованным") {
            ensureLoggedIn()
        }

        step("Открыть All News") {
            onView(withId(R.id.all_news_text_view))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun sortNews() {

        step("Авторизоваться или продолжить работу авторизованным") {
            ensureLoggedIn()
        }

        step("Открыть All News") {
            onView(withId(R.id.all_news_text_view))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }

        step("Нажать кнопку сортировки") {
            onView(withId(R.id.news_sort_button))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun filterNewsWithoutCategory() {

        step("Авторизоваться или продолжить работу авторизованным") {
            ensureLoggedIn()
        }

        step("Открыть All News") {
            onView(withId(R.id.all_news_text_view))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }

        step("Открыть панель фильтрации") {
            onView(withId(R.id.news_filter_button))
                .perform(click())

            onView(withId(R.id.filter_news_title_text_view))
                .check(matches(isDisplayed()))
        }

        step("Применить фильтр без выбора категории") {
            onView(withId(R.id.filter_news_apply_button))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun resetNewsFilter() {

        step("Авторизоваться или продолжить работу авторизованным") {
            ensureLoggedIn()
        }

        step("Открыть All News") {
            onView(withId(R.id.all_news_text_view))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }

        step("Открыть панель фильтрации") {
            onView(withId(R.id.news_filter_button))
                .perform(click())

            onView(withId(R.id.filter_news_title_text_view))
                .check(matches(isDisplayed()))
        }

        step("Отменить фильтрацию") {
            onView(withId(R.id.filter_news_cancel_button))
                .perform(click())

            onView(withId(R.id.news_list_app_bar))
                .check(matches(isDisplayed()))
        }

    }
}