package com.haksoftware.p9_da_real_estate_manager.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.PhotoItemBinding

/**
 * RecyclerView Adapter for displaying a list of photos with descriptions in a RecyclerView.
 * @param context The context in which the adapter is operating.
 * @param photoList The list of PhotoEntity objects to be displayed.
 */
class DetailsPhotoAdapter(
    private val context: Context,
    private val photoList: MutableList<PhotoEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return photoList.size
    }

    /**
     * ViewHolder class for the photo item view.
     * @param binding The binding for the photo item layout.
     */
    class ItemViewHolder(binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imageView: ImageView = binding.showImageView
        private val tvDesc: TextView = binding.imgDescription

        /**
         * Binds the PhotoEntity data to the view.
         * @param photo The PhotoEntity object containing the photo and its description.
         */
        fun bind(photo: PhotoEntity) {
            imageView.setImageURI(photo.namePhoto.toUri())
            tvDesc.text = photo.descriptionPhoto
        }
    }
}
