package com.haksoftware.p9_da_real_estate_manager.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.HeaderPhotoItemBinding
import com.haksoftware.p9_da_real_estate_manager.databinding.PhotoItemBinding
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.PhotoViewState
import java.io.File

class DetailsPhotoAdapter(
    private val context: Context,
    private val photoList: List<PhotoEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return     ItemViewHolder(binding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemViewHolder = holder as ItemViewHolder
        val currentItem = photoList[position]
        itemViewHolder.bind(currentItem)
        holder.itemView.setOnClickListener {
            val dialog = ImageSliderDialogFragment.newInstance(photoList, position)
            if (context is AppCompatActivity) {
                dialog.show(context.supportFragmentManager, "ImageSliderDialog")
            }
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    class ItemViewHolder(binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.showImageView
        val tvDesc: TextView = binding.imgDescription
        fun bind(photo: PhotoEntity) {
            val file = File(photo.namePhoto)
            Glide.with(imageView.context)
                .load(file)
                .into(imageView)
            tvDesc.text = photo.descriptionPhoto
        }
    }
}