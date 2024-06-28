package com.haksoftware.p9_da_real_estate_manager

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.haksoftware.p9_da_real_estate_manager.utils.NetworkConnectionRule
import com.haksoftware.realestatemanager.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IsInternetAvailableTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        // Ensure we start with a known state for each test
        setNetworkState(true)
    }
    @After
    fun tearDown() {
        // Reset network state to enabled after each test
        setNetworkState(true)
    }

    @Test
    fun isInternetNotAvailable() = runBlocking {
        setNetworkState(false)
        delay(2000)
        assertFalse(Utils.isInternetAvailable())
    }

    @Test
    fun isInternetAvailable() = runBlocking {
        setNetworkState(true)
        delay(2000)
        assertFalse(Utils.isInternetAvailable())
    }
    private fun setNetworkState(enable: Boolean) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation
        val wifiCommand = if (enable) "svc wifi enable" else "svc wifi disable"
        val dataCommand = if (enable) "svc data enable" else "svc data disable"

        uiAutomation.executeShellCommand(wifiCommand).close()
        uiAutomation.executeShellCommand(dataCommand).close()
    }
}