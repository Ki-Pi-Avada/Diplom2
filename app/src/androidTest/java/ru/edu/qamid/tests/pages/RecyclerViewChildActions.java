package ru.edu.qamid.tests.pages;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public final class RecyclerViewChildActions {
    private RecyclerViewChildActions() { }

    public static ViewAction clickChildViewWithId(int childViewId) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Нажать на дочерний элемент с id " + childViewId;
            }

            @Override
            public void perform(UiController uiController, View itemView) {
                View child = itemView.findViewById(childViewId);
                if (child == null || !child.isShown()) {
                    throw new IllegalStateException("Дочерний элемент новости недоступен: " + childViewId);
                }
                ViewActions.click().perform(uiController, child);
            }
        };
    }
}
