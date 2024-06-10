package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.PhotoItemBinding

class AddPhotoAdapter(
    private val photoList: MutableList<PhotoEntity>,
    private val removePhotoListener: RemovePhotoListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as ItemViewHolder
        val currentItem = photoList[position ]
        itemViewHolder.bind(currentItem, removePhotoListener)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    fun addPhoto(photoEntity: PhotoEntity) {
        photoList.add(photoEntity)
        notifyItemInserted(photoList.size )
    }
    fun removePhoto(photo: PhotoEntity) {
        val position = photoList.indexOf(photo)
        if (position != -1) {
            photoList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, photoList.size)
        }
    }
    inner class ItemViewHolder(binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imageView: ImageView = binding.showImageView
        private val tvDesc: TextView = binding.imgDescription
        private val imageButtonRemove: ImageButton = binding.imageButtonRemove
        fun bind(photo: PhotoEntity, removePhotoListener: RemovePhotoListener) {
            imageView.setImageURI(photo.namePhoto.toUri())
            tvDesc.text = photo.descriptionPhoto
            imageButtonRemove.visibility = View.VISIBLE
            imageButtonRemove.setOnClickListener {
                removePhotoListener.onPhotoRemoved(photo)
            }
        }
    }
}