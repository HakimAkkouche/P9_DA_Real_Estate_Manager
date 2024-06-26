package com.haksoftware.p9_da_real_estate_manager

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.ActivityTestRule
import com.haksoftware.p9_da_real_estate_manager.utils.NetworkConnectionRule
import com.haksoftware.p9_da_real_estate_manager.utils.RecyclerViewItemCountAssertion
import com.haksoftware.p9_da_real_estate_manager.utils.clickOnFirstChild
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    @get:Rule
    val networkConnectionRule = NetworkConnectionRule()
    @Before
    fun setUp() {
        Intents.init()
    }
    @After
    fun tearDown() {
        Intents.release()
    }
    @Test
    fun addRealEstateTest(){
        // Stub the camera intent
        val result = createImageCaptureStub()
        // Set up result stubbing
        intending(hasAction("android.media.action.IMAGE_CAPTURE")).respondWith(result)

        Thread.sleep(1000)

        // Vérifie que le bouton "Add" est affiché
        onView(withId(R.id.action_add))
            .check(matches(isDisplayed()))
            .perform(click())

        Thread.sleep(1000)
        // Vérifie que le fragment d'ajout est affiché
        onView(withId(R.id.add_fragment_root))
            .check(matches(isDisplayed()))


        onView(withId(R.id.button_add_image))
            .check(matches(isDisplayed()))
            .perform(click())
        Thread.sleep(1000)


        onView(withId(R.id.btnTakePhoto))
            .check(matches(isDisplayed()))
            .perform(click())
        Thread.sleep(1000)

        val appCompatEditDescPhoto = onView(
            withId(R.id.edit_desc_photo))
            .check(matches(isDisplayed()))
            .perform(replaceText("Description photo"), closeSoftKeyboard())

        onView(withId(R.id.submit_button_photo))
            .check(matches(isDisplayed()))
            .perform(click())
        Thread.sleep(1000)

        onView(
            withId(R.id.edit_price))
            .check(matches(isDisplayed()))
            .perform(replaceText("200000"), closeSoftKeyboard())

        onView(
            withId(R.id.edit_surface))
            .check(matches(isDisplayed()))
            .perform(replaceText("1000"), closeSoftKeyboard())

        Thread.sleep(1000)

        onView(allOf(withId(R.id.increment), withParent(withParent(withId(R.id.number_picker_room_count)))))
            .check(matches(isDisplayed())).perform(click())

        Thread.sleep(1000)
        onView(allOf(withId(R.id.increment), withParent(withParent(withId(R.id.number_picker_bathroom_count)))))
            .check(matches(isDisplayed())).perform(click())


        Thread.sleep(1000)
        onView(withId(R.id.add_fragment_root))
            .perform(swipeUp())

        onView(
            withId(R.id.edit_description))
            .check(matches(isDisplayed()))
            .perform(replaceText("Beautiful apartment in Paris."), closeSoftKeyboard())

        onView(
            withId(R.id.edit_address))
            .check(matches(isDisplayed()))
            .perform(replaceText("15 rue Marbeuf"), closeSoftKeyboard())

        onView(
            withId(R.id.edit_postal_code))
            .check(matches(isDisplayed()))
            .perform(replaceText("75008"), closeSoftKeyboard())

        onView(
            withId(R.id.edit_city))
            .check(matches(isDisplayed()))
            .perform(replaceText("PARIS"), closeSoftKeyboard())

        onView(
            withId(R.id.edit_country))
            .check(matches(isDisplayed()))
            .perform(replaceText("FRANCE"), closeSoftKeyboard())

        onView(withId(R.id.chip_group_point_of_interest))
            .perform(clickOnFirstChild())


        onView(withId(R.id.button_submit))
            .check(matches(isDisplayed()))
            .perform(click())
        Thread.sleep(1000)

        // Vérifie que le RecyclerView est affiché
        onView(withId(R.id.recycler_view_real_estates))
            .check(matches(isDisplayed()))

        // Vérifie le nombre d'éléments dans le RecyclerView
        onView(withId(R.id.recycler_view_real_estates))
            .check(RecyclerViewItemCountAssertion(expectedCount = 2))

        // Vérifie que le bouton "Search" est affiché
        onView(withId(R.id.action_search))
            .check(matches(isDisplayed()))
            .perform(click())
        Thread.sleep(1000)

        onView(withId(R.id.edit_price_max))
            .check(matches( isDisplayed()))
            .perform(replaceText("200000"))

        onView(withId(R.id.search_fragment_root))
            .check(matches(isDisplayed()))
            .perform(swipeUp())

        onView(withId( R.id.buttonSearch))
            .check(matches(isDisplayed()))
            .perform(click())
        // Vérifie que le RecyclerView est affiché
        onView(withId(R.id.recycler_view_real_estates))
            .check(matches(isDisplayed()))

        // Vérifie le nombre d'éléments dans le RecyclerView
        onView(withId(R.id.recycler_view_real_estates))
            .check(RecyclerViewItemCountAssertion(expectedCount = 1))
    }
    private fun createImageCaptureStub(): Instrumentation.ActivityResult {
        // Create a bitmap to return as the captured image
        val bitmap = BitmapFactory.decodeResource(
            activityRule.activity.resources,
            R.drawable.test_photo // Replace with your sample image resource
        )

        val resultData = Intent()
        resultData.putExtra("data", bitmap)
        return Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
    }

}