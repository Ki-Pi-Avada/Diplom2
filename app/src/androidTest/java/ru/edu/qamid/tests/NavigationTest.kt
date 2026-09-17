package ru.edu.qamid.tests

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.contrib.RecyclerViewActions
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import org.junit.Test
import ru.edu.qamid.R

@Feature("Навигация")
class NavigationTest : TestBase() {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-06. Переход в News через боковое меню.")
    fun openNewsFromMainMenu() {
        login()
        step("Открыть боковое меню") {
            onView(withId(R.id.main_menu_image_button))
                .perform(click())
                .check(matches(isDisplayed()))
        }
        step("Выбрать Новости") {
            onView(withText("Новости"))
                .perform(click())
            waitForObject("news_list_app_bar")
            onView(withId(R.id.news_list_app_bar)).check(matches(isDisplayed()))
        }
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-07. Открытие раздела тематических цитат по кнопке в верхней панели.")
    fun openOurMission() {
        login()
        step("Открыть раздел Наша Миссия / тематические цитаты") {
            onView(withId(R.id.our_mission_image_button)).perform(click())
            waitForObject("our_mission_title_text_view")
            onView(withId(R.id.our_mission_title_text_view))
                .check(matches(withText("Главное - жить любя")))
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-08. Переход в News через ALL NEWS на главном экране.")
    fun openNewsFromAllNews() {
        login()
        step("Нажать ALL NEWS") {
            onView(withId(R.id.all_news_text_view))
                .check(matches(isDisplayed()))
                .perform(click())
            waitForObject("news_list_app_bar")
            onView(withId(R.id.news_list_app_bar)).check(matches(isDisplayed()))
        }
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-07. Проверка раскрытия и сворачивания первой карточки тематических цитат.")
    fun expandAndCollapseOurMissionCard() {
        login()
        step("Открыть тематические цитаты") {
            onView(withId(R.id.our_mission_image_button)).perform(click())
            waitForObject("our_mission_item_list_recycler_view")
            onView(withId(R.id.our_mission_item_list_recycler_view))
                .check(matches(isDisplayed()))
        }
        step("Раскрыть первую карточку") {
            onView(withId(R.id.our_mission_item_list_recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        click()
                    )
                )
            onView(withText("\"Ну, идеальное устройство мира в моих глазах. Где никто не оценивает, никто не осудит, где говоришь, и тебя слышат, где, если страшно, тебя обнимут и возьмут за руку, а если холодно тебя согреют.” Юля Капис, волонтер"))
                .check(matches(isDisplayed()))
        }
        step("Свернуть первую карточку") {
            onView(withId(R.id.our_mission_item_list_recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        click()
                    )
                )
            onView(withText("\"Ну, идеальное устройство мира в моих глазах. Где никто не оценивает, никто не осудит, где говоришь, и тебя слышат, где, если страшно, тебя обнимут и возьмут за руку, а если холодно тебя согреют.” Юля Капис, волонтер"))
                .check(androidx.test.espresso.assertion.ViewAssertions.doesNotExist())
        }
    }
}
