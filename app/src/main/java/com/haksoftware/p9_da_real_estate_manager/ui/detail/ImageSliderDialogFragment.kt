package com.haksoftware.p9_da_real_estate_manager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentImageSliderDialogBinding

class ImageSliderDialogFragment(private val photoList: List<PhotoEntity>, private val startPosition: Int) : DialogFragment() {

    lateinit var binding: FragmentImageSliderDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageSliderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager

        val adapter = ImageViewPagerAdapter(requireContext(), photoList)
        viewPager.adapter = adapter
        viewPager.currentItem = startPosition

    }

    companion object {
        fun newInstance(photoList: List<PhotoEntity>, startPosition: Int): ImageSliderDialogFragment {
            return ImageSliderDialogFragment(photoList, startPosition)
        }
    }
}
