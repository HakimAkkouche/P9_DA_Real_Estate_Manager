package com.haksoftware.p9_da_real_estate_manager

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.material.chip.ChipGroup
import com.haksoftware.p9_da_real_estate_manager.data.manager.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.utils.NetworkConnectionRule
import com.haksoftware.p9_da_real_estate_manager.utils.RecyclerViewItemCountAssertion
import com.haksoftware.realestatemanager.utils.Utils
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DetailsFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun detailsAreDisplayedTest() {

        Thread.sleep(1000)
        // Vérifie que la RecyclerView est affichée
        onView(withId(R.id.recycler_view_real_estates))
            .check(matches(isDisplayed()))

        Thread.sleep(500)
        // Clique sur le premier élément de la liste
        onView(withId(R.id.recycler_view_real_estates))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        Thread.sleep(1000)
        // Vérifie que le panneau de détails est ouvert
        onView(withId(R.id.sliding_pane_layout))
            .check(matches(isDisplayed()))

        Thread.sleep(1000)
        // Vérifie que le texte du titre est affiché
        onView(withId(R.id.text_price))
            .check(matches(isDisplayed()))
            .check(matches(withText("$ 250,000")))

        // Vérifie que l'image du bien immobilier est affichée
        onView(withId(R.id.photo_gallery))
            .check(matches(isDisplayed()))
            .check(RecyclerViewItemCountAssertion(expectedCount = 1))

        onView(withId(R.id.text_creation_date))
            .check(matches(isDisplayed()))
            .check(matches(withText("25/05/2024")))

        // Vérifie que la description du bien immobilier est affichée
        onView(withId(R.id.text_price_euro))
            .check(matches(isDisplayed()))
            .check(matches(withText("232 500 €")))

        // Vérifie que le bouton "edit" est affiché
        onView(withId(R.id.action_edit))
            .check(matches(isDisplayed()))

        onView(withId(R.id.text_description))
            .check(matches(isDisplayed()))
            .check(matches(withText("Beautiful spacious apartment in downtown.")))

        onView(withId(R.id.text_type))
            .check(matches(isDisplayed()))
            .check(matches(withText("Apartment")))

        onView(withId(R.id.text_square_feet))
            .check(matches(isDisplayed()))
            .check(matches(withText("1200")))


        onView(withId(R.id.text_room_count))
            .check(matches(isDisplayed()))
            .check(matches(withText("3")))

        onView(withId(R.id.text_bathroom_count))
            .check(matches(isDisplayed()))
            .check(matches(withText("2")))

        // Vérifie que le ChipGroup est visible
        onView(withId(R.id.chip_group_point_of_interest))
            .check(matches(isDisplayed()))

        // Vérifie le nombre de Chips dans le ChipGroup
        onView(withId(R.id.chip_group_point_of_interest))
            .check(matches(withChipCount(1)))

        onView(withId(R.id.scroll_view_details))
            .perform(swipeUp())

        onView(withId(R.id.text_address))
            .check(matches(isDisplayed()))
            .check(matches(withText("15 rue Blomet")))

        onView(withId(R.id.text_postal_code_city))
            .check(matches(isDisplayed()))
            .check(matches(withText("75015 PARIS")))

        onView(withId(R.id.text_state))
            .check(matches(isDisplayed()))
            .check(matches(withText("FRANCE")))

        onView(withId(R.id.text_realtor))
            .check(matches(isDisplayed()))
            .check(matches(withText("Mr DOE John")))

        onView(withId(R.id.text_realtor_number))
            .check(matches(isDisplayed()))
            .check(matches(withText("1234567890")))

        onView(withId(R.id.text_realtor_email))
            .check(matches(isDisplayed()))
            .check(matches(withText("john.doe@example.com")))
    }
    // Custom matcher to count children in a ViewGroup
    private fun withChipCount(expectedCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("with chip count: $expectedCount")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view !is ChipGroup) return false
                return view.childCount == expectedCount
            }
        }
    }
}
