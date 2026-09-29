package ru.edu.qamid.tests;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Searchable;
import androidx.test.uiautomator.SearchCondition;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Rule;

import static org.junit.Assert.assertTrue;

import io.qameta.allure.android.rules.ScreenshotRule;

import ru.edu.qamid.ui.AppActivity;

public abstract class TestBase {

    protected UiDevice device;

    @Rule
    public ActivityScenarioRule<AppActivity> activityRule =
            new ActivityScenarioRule<>(AppActivity.class);

    @Rule
    public ScreenshotRule screenshotRule =
            new ScreenshotRule(
                    ScreenshotRule.Mode.FAILURE,
                    "screenshot-on-failure"
            );

    @Before
    public void setUp() {
        device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation()
        );

        boolean initialScreenOpened = device.wait(new SearchCondition<Boolean>() {
            @Override
            public Boolean apply(Searchable ignored) {
                return device.hasObject(By.res("ru.edu.qamid:id/auth_app_bar"))
                        || device.hasObject(By.res("ru.edu.qamid:id/main_app_bar"));
            }
        }, 60_000);
        assertTrue("Приложение не открыло экран авторизации или главный экран",
                initialScreenOpened);

    }

    protected boolean waitForObject(String resourceId) {
        return device.wait(
                Until.hasObject(
                        By.res("ru.edu.qamid:id/" + resourceId)
                ),
                10_000
        );
    }

    protected boolean waitForText(String text) {
        return device.wait(
                Until.hasObject(By.text(text)),
                10_000
        );
    }
}
