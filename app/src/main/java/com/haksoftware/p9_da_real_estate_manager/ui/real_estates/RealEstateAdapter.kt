package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.EmptyItemBinding
import com.haksoftware.p9_da_real_estate_manager.databinding.RealEstateItemBinding
import com.haksoftware.realestatemanager.utils.Utils.formatNumberToUSStyle
import com.haksoftware.realestatemanager.utils.Utils.isDateWithinLast7Days

/**
 * Adapter class for displaying real estate items in a RecyclerView.
 * @property onItemClicked Callback function to handle item clicks.
 */
class RealEstateAdapter(private val onItemClicked: (RealEstateWithDetails) -> Unit) :
    ListAdapter<RealEstateWithDetails, RecyclerView.ViewHolder>(RealEstateDiffCallback) {

    private var listItemView: MutableList<MaterialCardView> = arrayListOf()

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1

        /**
         * DiffUtil callback for calculating the difference between two non-null items in a list.
         */
        private val RealEstateDiffCallback = object : DiffUtil.ItemCallback<RealEstateWithDetails>() {
            override fun areItemsTheSame(oldItem: RealEstateWithDetails, newItem: RealEstateWithDetails): Boolean {
                return oldItem.realEstate.idRealEstate == newItem.realEstate.idRealEstate
            }

            override fun areContentsTheSame(oldItem: RealEstateWithDetails, newItem: RealEstateWithDetails): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * Returns the view type of the item at position for the purposes of view recycling.
     */
    override fun getItemViewType(position: Int): Int {
        return if (currentList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val binding = EmptyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmptyViewHolder(binding)
        } else {
            val binding = RealEstateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RealEstateViewHolder(binding)
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RealEstateViewHolder) {
            val realEstate = getItem(position)
            listItemView.add(holder.itemView as MaterialCardView)
            holder.itemView.setOnClickListener {
                onItemClicked(realEstate)
            }
            holder.bind(realEstate)
        } else if (holder is EmptyViewHolder) {
            holder.bind()
        }
    }

    /**
     * ViewHolder for real estate items.
     */
    inner class RealEstateViewHolder(private val binding: RealEstateItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imageView: ImageView = binding.realEstateThumbnail

        /**
         * Binds the real estate data to the view.
         */
        fun bind(realEstateWithDetails: RealEstateWithDetails) {
            binding.type.text = realEstateWithDetails.type.nameType
            binding.city.text = realEstateWithDetails.realEstate.city
            binding.price.text = formatNumberToUSStyle(realEstateWithDetails.realEstate.price)
            binding.surface.text = realEstateWithDetails.realEstate.squareFeet.toString()
            binding.roomCount.text = realEstateWithDetails.realEstate.roomCount.toString()

            if (realEstateWithDetails.realEstate.soldDate != null) {
                binding.currentState.setImageResource(R.drawable.ic_sold)
            } else if (isDateWithinLast7Days(realEstateWithDetails.realEstate.creationDate)) {
                binding.currentState.setImageResource(R.drawable.ic_new)
            }

            val photoPath = realEstateWithDetails.photos.getOrNull(0)?.namePhoto
            if (photoPath != null) {
                imageView.setImageURI(photoPath.toUri())
            } else {
                imageView.setImageResource(R.drawable.home_24)
            }
        }
    }

    /**
     * ViewHolder for empty state.
     */
    inner class EmptyViewHolder(private val binding: EmptyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds the empty view text.
         */
        fun bind() {
            binding.emptyText.text = binding.root.context.getString(R.string.empty)
        }
    }
}
