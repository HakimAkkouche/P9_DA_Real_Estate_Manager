package com.haksoftware.p9_da_real_estate_manager.ui.loan_simulator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.haksoftware.realestatemanager.utils.Utils.formatNumberToUSStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.pow

class LoanSimulatorViewModel(application: Application) : AndroidViewModel(application) {

    private val _loanAmount = MutableStateFlow(0.0)
    private val _interestRate = MutableStateFlow(0.0)
    private val _loanDuration = MutableStateFlow(0.0)
    private val _monthlyPayment = MutableStateFlow("")
    private val _totalCost = MutableStateFlow("")
    private val _interestCost = MutableStateFlow("")

    val loanAmount: StateFlow<Double> = _loanAmount
    val interestRate: StateFlow<Double> = _interestRate
    val loanDuration: StateFlow<Double> = _loanDuration
    val monthlyPayment: StateFlow<String> get() = _monthlyPayment
    val totalCost: StateFlow<String> get() = _totalCost
    val interestCost: StateFlow<String> get() = _interestCost

    val isFormValid: StateFlow<Boolean> = combine(
        _loanAmount, _interestRate, _loanDuration
    ) { values: Array<Double> ->
        values[0] != 0.0 && values[1] != 0.0 && values[2] != 0.0
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )
    fun updateLoanAmount(loanAmount: Double) {
        _loanAmount.value = loanAmount
    }
    fun updateInterestRate(interestRate: Double) {
        _interestRate.value = interestRate
    }
    fun updateLoanDuration(loanDuration: Double) {
        _loanDuration.value = loanDuration
    }

    fun calculateMonthlyPayment() {

        val monthlyInterestRate = interestRate.value / 100 / 12
        val numberOfPayments = loanDuration.value * 12
        val monthlyPayment = loanAmount.value * monthlyInterestRate / (1 - (1 + monthlyInterestRate).pow(
            -numberOfPayments
        ))
        _monthlyPayment.value = "Mensualité: %s".format(formatNumberToUSStyle((monthlyPayment).toInt()))
        _totalCost.value = "Total cost : %s".format(formatNumberToUSStyle((monthlyPayment * numberOfPayments).toInt()))
        _interestCost.value =
            "Loan cost : %s".format(formatNumberToUSStyle((monthlyPayment * numberOfPayments - loanAmount.value).toInt()))
    }
}