package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.EmptyItemBinding
import com.haksoftware.p9_da_real_estate_manager.databinding.RealEstateItemBinding
import java.io.File

class RealEstateAdapter(private val clickListener: OnItemClickListener) : ListAdapter<RealEstateWithDetails, RecyclerView.ViewHolder>(RealEstateDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val binding = EmptyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmptyViewHolder(binding)
        } else {
            val binding = RealEstateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RealEstateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RealEstateViewHolder) {
            val realEstate = getItem(position)
            holder.bind(realEstate)
        } else if (holder is EmptyViewHolder) {
            holder.bind()
        }
    }

    inner class RealEstateViewHolder( binding: RealEstateItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val binding = binding

        private val imageView: ImageView = binding.realEstateThumbnail

        fun bind(realEstate: RealEstateWithDetails) {
            binding.type.text = realEstate.type.nameType
            binding.city.text = realEstate.realEstate.city
            val price = "${realEstate.realEstate.price} $"
            binding.price.text = price

            val photoPath = realEstate.photos.getOrNull(0)?.namePhoto
            if (photoPath != null) {
                val file = File(photoPath)
                Glide.with(imageView.context)
                    .load(file)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.home_24) // Optionnel : Image par défaut si aucune photo
            }
            itemView.setOnClickListener {
                clickListener.onItemClick(realEstate)
            }
        }
    }

    inner class EmptyViewHolder(private val binding: EmptyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.emptyText.text = binding.root.context.getString(R.string.empty)
        }
    }

    class RealEstateDiffCallback : DiffUtil.ItemCallback<RealEstateWithDetails>() {
        override fun areItemsTheSame(oldItem: RealEstateWithDetails, newItem: RealEstateWithDetails): Boolean {
            return oldItem.realEstate.idRealEstate == newItem.realEstate.idRealEstate
        }

        override fun areContentsTheSame(oldItem: RealEstateWithDetails, newItem: RealEstateWithDetails): Boolean {
            return oldItem == newItem
        }
    }
}