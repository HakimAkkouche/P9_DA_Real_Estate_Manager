package com.haksoftware.p9_da_real_estate_manager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentImageSliderDialogBinding

/**
 * DialogFragment for displaying a list of images in a slider.
 * @param photoList List of PhotoEntity objects to be displayed in the slider.
 * @param startPosition The starting position of the slider.
 */
class ImageSliderDialogFragment(private val photoList: List<PhotoEntity>, private val startPosition: Int) : DialogFragment() {

    lateinit var binding: FragmentImageSliderDialogBinding

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageSliderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager

        val adapter = ImageViewPagerAdapter(photoList)
        viewPager.adapter = adapter
        viewPager.currentItem = startPosition
    }

    companion object {
        /**
         * Creates a new instance of the ImageSliderDialogFragment.
         * @param photoList List of PhotoEntity objects to be displayed in the slider.
         * @param startPosition The starting position of the slider.
         * @return A new instance of ImageSliderDialogFragment.
         */
        fun newInstance(photoList: List<PhotoEntity>, startPosition: Int): ImageSliderDialogFragment {
            return ImageSliderDialogFragment(photoList, startPosition)
        }
    }
}
