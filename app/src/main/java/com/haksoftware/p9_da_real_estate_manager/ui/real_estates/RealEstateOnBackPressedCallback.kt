package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.slidingpanelayout.widget.SlidingPaneLayout


/**
 * Custom OnBackPressedCallback for handling back button presses when a SlidingPaneLayout is open.
 */
class RealEstateOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout, private val activity: FragmentActivity
) : OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen), SlidingPaneLayout.PanelSlideListener {

    /**
     * Handles the back button press to close the SlidingPaneLayout.
     */
    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    /**
     * Called when a detail view's position changes.
     */
    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    /**
     * Called when a detail view becomes slid completely open.
     */
    override fun onPanelOpened(panel: View) {
        isEnabled = true
        activity.invalidateOptionsMenu()
    }

    /**
     * Called when a detail view becomes slid completely closed.
     */
    override fun onPanelClosed(panel: View) {
        isEnabled = false
        activity.invalidateOptionsMenu()
    }

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }
}
