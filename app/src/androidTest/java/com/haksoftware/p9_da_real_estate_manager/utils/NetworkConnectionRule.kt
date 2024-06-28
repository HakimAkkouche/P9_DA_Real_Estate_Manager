package com.haksoftware.p9_da_real_estate_manager.utils

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NetworkConnectionRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                // Disable WiFi and mobile data
                setNetworkState(false)
                try {
                    base.evaluate() // Execute the test
                } finally {
                    // Re-enable WiFi and mobile data
                    setNetworkState(true)
                }
            }
        }
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