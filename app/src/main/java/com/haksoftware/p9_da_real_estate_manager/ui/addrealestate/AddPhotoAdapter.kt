package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.haksoftware.p9_da_real_estate_manager.databinding.HeaderPhotoItemBinding
import com.haksoftware.p9_da_real_estate_manager.databinding.PhotoItemBinding

class AddPhotoAdapter(
    private val context: Context,
    private val photoList: MutableList<PhotoViewState>,
    private val addPhotoClickListener: AddPhotoListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AddPhotoDialogListener {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = HeaderPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        }
    }    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_HEADER) {
            val headerViewHolder = holder as HeaderViewHolder

            headerViewHolder.btnAddImage.setOnClickListener {
                val dialog = AddPhotoDialog()
                dialog.setListener(this)
                dialog.show((context as AppCompatActivity).supportFragmentManager, "AddPhotoDialog")
            }
        } else {
            val itemViewHolder = holder as ItemViewHolder
            val currentItem = photoList[position - 1]
            itemViewHolder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        // Ajouter 1 pour l'en-tête
        return photoList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }


    class HeaderViewHolder(binding: HeaderPhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val btnAddImage: ImageButton = binding.btnAddImage
    }

    class ItemViewHolder(binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.showImageView
        val tvDesc: TextView = binding.imgDescription
        fun bind(photo: PhotoViewState) {
            imageView.setImageURI(photo.imageUri)
            tvDesc.text = photo.description
        }
    }

    override fun onPhotoDialogAdded(photoViewState: PhotoViewState) {
        photoList.add(photoViewState)
        notifyItemInserted(photoList.size - 1)
        addPhotoClickListener.onPhotoAdded(photoViewState)
    }
}