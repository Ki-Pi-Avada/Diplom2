package ru.edu.qamid.tests

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher : TypeSafeMatcher<Root>() {

    override fun describeTo(description: Description) {
        description.appendText("is a Toast")
    }

    override fun matchesSafely(root: Root): Boolean {
        val windowType = root.windowLayoutParams.get().type

        if (windowType == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken = root.decorView.windowToken
            val appToken = root.decorView.applicationWindowToken

            return windowToken == appToken || windowToken == null
        }

        return false
    }
}