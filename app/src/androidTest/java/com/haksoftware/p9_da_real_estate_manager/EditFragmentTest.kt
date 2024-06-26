package com.haksoftware.p9_da_real_estate_manager

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.haksoftware.p9_da_real_estate_manager.utils.NetworkConnectionRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    @get:Rule
    val networkConnectionRule = NetworkConnectionRule()

    @Test
    fun EditFragmentTest() {

        Thread.sleep(1000)
        // Vérifie que la RecyclerView est affichée
        onView(withId(R.id.recycler_view_real_estates))
            .check(matches(isDisplayed()))

        Thread.sleep(500)
        // Clique sur le premier élément de la liste
        onView(withId(R.id.recycler_view_real_estates))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        Thread.sleep(1000)
        // Vérifie que le panneau de détails est ouvert
        onView(withId(R.id.sliding_pane_layout))
            .check(matches(isDisplayed()))

        // Vérifie que le bouton "edit" est affiché
        onView(withId(R.id.action_edit))
            .check(matches(isDisplayed()))
        onView(withId(R.id.action_edit)).perform(click())

        Thread.sleep(1000)

        val appCompatEditText = onView(
                withId(R.id.edit_description))
            .check(matches(isDisplayed()))

        appCompatEditText.perform(replaceText("Beautiful spacious apartment in Paris."), closeSoftKeyboard())

        onView(withId(R.id.add_fragment_root))
            .perform(swipeUp())

        onView(withId(R.id.button_submit))
            .check(matches(isDisplayed()))
        onView(withId(R.id.button_submit)).perform(click())

        Thread.sleep(1000)
        val textDescription = onView(withId(R.id.text_description))
            .check(matches(isDisplayed()))
        textDescription.check(matches(withText("Beautiful spacious apartment in Paris.")))

    }
}