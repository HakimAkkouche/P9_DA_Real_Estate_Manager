package com.haksoftware.p9_da_real_estate_manager.utils

/**
 * Utility class to resolve configuration settings for the build.
 */
class BuildConfigResolver {
    /**
     * Checks if the application is currently running an Android test.
     * @return Boolean indicating whether the application is running an Android test.
     */
    val isRunningAndroidTest: Boolean
        get() = try {
            // Try to find the Espresso class to determine if the test is running
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
}
