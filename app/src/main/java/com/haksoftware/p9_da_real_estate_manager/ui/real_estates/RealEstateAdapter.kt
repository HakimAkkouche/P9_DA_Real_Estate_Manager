package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.EmptyItemBinding
import com.haksoftware.p9_da_real_estate_manager.databinding.RealEstateItemBinding
import com.haksoftware.realestatemanager.utils.Utils.formatNumberToUSStyle
import com.haksoftware.realestatemanager.utils.Utils.isDateWithinLast7Days
import java.io.File


class RealEstateAdapter(private val onItemClicked: (RealEstateWithDetails) -> Unit) :
    ListAdapter<RealEstateWithDetails, RecyclerView.ViewHolder>(RealEstateDiffCallback) {

        private var listItemView: MutableList<MaterialCardView> = arrayListOf()

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1

        private val RealEstateDiffCallback = object : DiffUtil.ItemCallback<RealEstateWithDetails>() {
            override fun areItemsTheSame(oldItem: RealEstateWithDetails, newItem: RealEstateWithDetails): Boolean {
                return (oldItem.realEstate.idRealEstate == newItem.realEstate.idRealEstate
                        )
            }

            override fun areContentsTheSame(oldItem: RealEstateWithDetails, newItem: RealEstateWithDetails): Boolean {
                return oldItem == newItem
            }
        }
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
           listItemView.add(holder.itemView as MaterialCardView)
            holder.itemView.setOnClickListener {
                /* for (itemView in listItemView) {
                    (itemView).setCardBackgroundColor(itemView.context.getResources().getColor(R.color.white))

                }
                (holder.itemView as MaterialCardView).setCardBackgroundColor(
                    holder.itemView.context.resources.getColor(R.color.green_500))*/
                onItemClicked(realEstate)
            }
            holder.bind(realEstate)
        } else if (holder is EmptyViewHolder) {
            holder.bind()
        }
    }

    inner class RealEstateViewHolder(private val binding: RealEstateItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imageView: ImageView = binding.realEstateThumbnail

        fun bind(realEstateWithDetails: RealEstateWithDetails) {
            binding.type.text = realEstateWithDetails.type.nameType
            binding.city.text = realEstateWithDetails.realEstate.city
            binding.price.text = formatNumberToUSStyle(realEstateWithDetails.realEstate.price)
            binding.surface.text = realEstateWithDetails.realEstate.squareFeet.toString()
            binding.roomCount.text = realEstateWithDetails.realEstate.roomCount.toString()

            if (realEstateWithDetails.realEstate.soldDate != null) {
                binding.currentState.setImageResource(R.drawable.ic_sold)
            }
            else if(isDateWithinLast7Days(realEstateWithDetails.realEstate.creationDate) ){
                binding.currentState.setImageResource(R.drawable.ic_new)
            }

            val photoPath = realEstateWithDetails.photos.getOrNull(0)?.namePhoto
            if (photoPath != null) {
                val file = File(photoPath)
                Glide.with(imageView.context)
                    .load(file)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.home_24) // Optionnel : Image par défaut si aucune photo
            }
        }
    }

    inner class EmptyViewHolder(private val binding: EmptyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.emptyText.text = binding.root.context.getString(R.string.empty)
        }
    }

}