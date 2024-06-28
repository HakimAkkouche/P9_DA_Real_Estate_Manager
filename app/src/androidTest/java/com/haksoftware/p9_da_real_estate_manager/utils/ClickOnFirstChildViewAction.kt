package com.haksoftware.p9_da_real_estate_manager.utils

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

class ClickOnFirstChildViewAction : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(ViewGroup::class.java)
    }

    override fun getDescription(): String {
        return "Click on the first child view of the ViewGroup"
    }

    override fun perform(uiController: UiController, view: View) {
        val viewGroup = view as ViewGroup
        if (viewGroup.childCount > 0) {
            viewGroup.getChildAt(0).performClick()
        }
    }
}

fun clickOnFirstChild(): ViewAction {
    return ClickOnFirstChildViewAction()
}