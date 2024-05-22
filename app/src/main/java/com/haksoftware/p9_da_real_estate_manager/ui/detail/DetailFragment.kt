package com.haksoftware.p9_da_real_estate_manager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentDetailBinding
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null

    private lateinit var realEstateWithDetails: RealEstateWithDetails
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val navigationArgs: DetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        val detailViewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        realEstateWithDetails = navigationArgs.realEstate
        binding.textTypeId.text = realEstateWithDetails.type.nameType
        binding.textCity.text = realEstateWithDetails.realEstate.city
        val price = "${realEstateWithDetails.realEstate.price} $"
        binding.textPrice.text = price

        /*realEstate = DetailFragmentA
            DetailFragmentArgs.fromBundle(requireArguments()).realEstate*/

        // Afficher les détails de l'immobilier

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}