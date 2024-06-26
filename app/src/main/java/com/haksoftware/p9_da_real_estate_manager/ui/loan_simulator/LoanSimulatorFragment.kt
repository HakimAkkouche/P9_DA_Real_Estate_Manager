package com.haksoftware.p9_da_real_estate_manager.ui.loan_simulator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentLoanSimulatorBinding
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.LoanSimulatorViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import kotlinx.coroutines.launch

/**
 * Fragment to simulate loan calculations.
 */
class LoanSimulatorFragment : Fragment() {

    private lateinit var binding: FragmentLoanSimulatorBinding
    private lateinit var viewModel: LoanSimulatorViewModel

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoanSimulatorBinding.inflate(inflater, container, false)
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoanSimulatorViewModel::class.java]

        setupTextWatchers()
        setupObservers()
        binding.btnCalculate.setOnClickListener {
            viewModel.calculateMonthlyPayment()
        }

        return binding.root
    }

    /**
     * Sets up observers for the LiveData and StateFlow objects in the ViewModel.
     */
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.isFormValid.collect { isValid ->
                binding.btnCalculate.isEnabled = isValid
            }
        }

        lifecycleScope.launch {
            viewModel.monthlyPayment.collect { monthlyPayment ->
                binding.textviewMonthlyPayment.text = monthlyPayment
            }
        }

        lifecycleScope.launch {
            viewModel.totalCost.collect { totalCost ->
                binding.textviewTotalCost.text = totalCost
            }
        }

        lifecycleScope.launch {
            viewModel.interestCost.collect { interestCost ->
                binding.textviewInterestCost.text = interestCost
            }
        }
    }

    /**
     * Sets up TextWatchers for the EditText fields to update the ViewModel.
     */
    private fun setupTextWatchers() {
        binding.edittextLoanAmount.addTextChangedListener(createTextWatcher {
            if (it.isNotEmpty()) {
                viewModel.updateLoanAmount(it.toDouble())
            }
        })
        binding.edittextLoanDuration.addTextChangedListener(createTextWatcher {
            if (it.isNotEmpty()) {
                viewModel.updateLoanDuration(it.toDouble())
            }
        })
        binding.edittextInterestRate.addTextChangedListener(createTextWatcher {
            if (it.isNotEmpty() && it != ",") {
                viewModel.updateInterestRate(it.toDouble())
            }
        })
    }

    /**
     * Creates a TextWatcher to handle text changes and update the ViewModel.
     */
    private fun createTextWatcher(update: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                update(s.toString())
            }
        }
    }
}
