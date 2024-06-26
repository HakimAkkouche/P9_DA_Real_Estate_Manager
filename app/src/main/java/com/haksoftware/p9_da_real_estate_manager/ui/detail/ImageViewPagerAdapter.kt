package com.haksoftware.p9_da_real_estate_manager.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.ItemImageSliderBinding

class ImageViewPagerAdapter(private val photoList: List<PhotoEntity>) : RecyclerView.Adapter<ImageViewPagerAdapter.ViewPagerViewHolder>() {

    override fun getItemCount(): Int {
        return photoList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.setData(photoList[position])

    }
    inner class ViewPagerViewHolder(val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(photo: PhotoEntity) {

            Glide.with(binding.root.context)
                .load(photo.namePhoto)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageView)
            binding.textDescription.text = photo.descriptionPhoto
        }
    }
}
