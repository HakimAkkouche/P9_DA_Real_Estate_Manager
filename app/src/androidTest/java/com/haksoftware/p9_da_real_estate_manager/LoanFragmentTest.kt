package com.haksoftware.p9_da_real_estate_manager

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoanFragmentTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loanCalculatedTest() {
        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Ouvrir le panneau de navigation"),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val recyclerView = onView(
            allOf(
                withId(com.google.android.material.R.id.design_navigation_view),
                isDisplayed()
            )
        )
        recyclerView.check(matches(isDisplayed()))

        val navigationMenuItemView = onView(
            allOf(
                withId(R.id.nav_loan),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.edittext_loan_amount),
                isDisplayed(),
            )
        )
        appCompatEditText.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.edittext_loan_amount),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("200000"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.edittext_loan_duration),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("25"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.edittext_interest_rate),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("3.5"), closeSoftKeyboard())

        val button = onView(
            allOf(
                withId(R.id.btn_calculate), withText("LAUNCH SIMULATION"),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val materialButton = onView(
            allOf(
                withId(R.id.btn_calculate), withText("Launch simulation"),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.textview_monthly_payment), withText("Monthly payment : $ 1,001"),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Monthly payment : $ 1,001")))

        val textView2 = onView(
            allOf(
                withId(R.id.textview_total_cost), withText("Total cost : $ 300,374"),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Total cost : $ 300,374")))

        val textView3 = onView(
            allOf(
                withId(R.id.textview_interest_cost), withText("Loan cost : $ 100,374"),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Loan cost : $ 100,374")))
    }
}
