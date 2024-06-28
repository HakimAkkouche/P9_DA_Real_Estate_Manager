package com.haksoftware.p9_da_real_estate_manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haksoftware.realestatemanager.utils.Utils.formatNumberToUSStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.pow

/**
 * ViewModel for the Loan Simulator. This ViewModel manages the state and logic for calculating loan payments.
 */
class LoanSimulatorViewModel : ViewModel() {

    private val _loanAmount = MutableStateFlow(0.0)
    private val _interestRate = MutableStateFlow(0.0)
    private val _loanDuration = MutableStateFlow(0.0)
    private val _monthlyPayment = MutableStateFlow("")
    private val _totalCost = MutableStateFlow("")
    private val _interestCost = MutableStateFlow("")

    private val loanAmount: StateFlow<Double> = _loanAmount
    private val interestRate: StateFlow<Double> = _interestRate
    private val loanDuration: StateFlow<Double> = _loanDuration
    val monthlyPayment: StateFlow<String> get() = _monthlyPayment
    val totalCost: StateFlow<String> get() = _totalCost
    val interestCost: StateFlow<String> get() = _interestCost

    /**
     * Checks if the form is valid based on the loan amount, interest rate, and loan duration.
     * @return Boolean indicating whether the form is valid.
     */
    val isFormValid: StateFlow<Boolean> = combine(
        _loanAmount, _interestRate, _loanDuration
    ) { values: Array<Double> ->
        values[0] != 0.0 && values[1] != 0.0 && values[2] != 0.0
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    /**
     * Updates the loan amount.
     * @param loanAmount The amount of the loan.
     */
    fun updateLoanAmount(loanAmount: Double) {
        _loanAmount.value = loanAmount
    }

    /**
     * Updates the interest rate.
     * @param interestRate The interest rate of the loan.
     */
    fun updateInterestRate(interestRate: Double) {
        _interestRate.value = interestRate
    }

    /**
     * Updates the loan duration.
     * @param loanDuration The duration of the loan in years.
     */
    fun updateLoanDuration(loanDuration: Double) {
        _loanDuration.value = loanDuration
    }

    /**
     * Calculates the monthly payment, total cost, and loan cost based on the current loan amount, interest rate, and loan duration.
     */
    fun calculateMonthlyPayment() {
        val monthlyInterestRate = interestRate.value / 100 / 12
        val numberOfPayments = loanDuration.value * 12
        val monthlyPayment = loanAmount.value * monthlyInterestRate / (1 - (1 + monthlyInterestRate).pow(-numberOfPayments))
        _monthlyPayment.value = "Monthly payment : %s".format(formatNumberToUSStyle((monthlyPayment).toInt()))
        _totalCost.value = "Total cost : %s".format(formatNumberToUSStyle((monthlyPayment * numberOfPayments).toInt()))
        _interestCost.value = "Loan cost : %s".format(formatNumberToUSStyle((monthlyPayment * numberOfPayments - loanAmount.value).toInt()))
    }
}
