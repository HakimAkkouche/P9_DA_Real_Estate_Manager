package com.haksoftware.p9_da_real_estate_manager


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.haksoftware.p9_da_real_estate_manager.utils.RecyclerViewItemCountAssertion
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RealEstateFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    @Test
    fun realEstatesFragmentShowsListTest() {
        Thread.sleep(1000)
        // Vérifie que le RecyclerView est affiché
        onView(withId(R.id.recycler_view_real_estates))
            .check(matches(isDisplayed()))

        // Vérifie le nombre d'éléments dans le RecyclerView
        onView(withId(R.id.recycler_view_real_estates))
            .check(RecyclerViewItemCountAssertion(expectedCount = 2))

        // Vérifie que le bouton "Add" est affiché
        onView(withId(R.id.action_add))
            .check(matches(isDisplayed()))

        // Vérifie que le bouton "Search" est affiché
        onView(withId(R.id.action_search))
            .check(matches(isDisplayed()))

        // Clique sur le bouton "Add"
        onView(withId(R.id.action_add))
            .perform(click())
        Thread.sleep(1000)
        // Vérifie que le fragment d'ajout est affiché
        onView(withId(R.id.add_fragment_root))
            .check(matches(isDisplayed()))

        // Retourne à l'activité principale
        pressBack()

        Thread.sleep(1000)
        // Clique sur le bouton "Search"
        onView(withId(R.id.action_search))
            .perform(click())

        Thread.sleep(1000)
        // Vérifie que le fragment de recherche est affiché
        onView(withId(R.id.search_fragment_root))
            .check(matches(isDisplayed()))

    }
}