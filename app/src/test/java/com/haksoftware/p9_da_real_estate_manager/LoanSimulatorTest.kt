package com.haksoftware.p9_da_real_estate_manager

import android.app.Application
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.LoanSimulatorViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.StateFlowTestUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class LoanSimulatorTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val application = Mockito.mock(Application::class.java)
    private lateinit var viewModel: LoanSimulatorViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoanSimulatorViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `is form valid when all fields are non-zero`() = runTest{

        viewModel.updateLoanAmount(10000.0)
        viewModel.updateInterestRate(5.0)
        viewModel.updateLoanDuration(10.0)
        viewModel.isFormValid

        assertTrue(StateFlowTestUtils.getValueForTesting(viewModel.isFormValid))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `is form invalid when one field is zero`() = runTest {
        viewModel.updateLoanAmount(0.0)
        viewModel.updateInterestRate(5.0)
        viewModel.updateLoanDuration(10.0)

        assertFalse(StateFlowTestUtils.getValueForTesting(viewModel.isFormValid))
    }
    @Test
    fun `calculate monthly payment computes correct values`() {
        viewModel.updateLoanAmount(10000.0)
        viewModel.updateInterestRate(5.0)
        viewModel.updateLoanDuration(10.0)
        viewModel.calculateMonthlyPayment()

        assertEquals("Monthly payment : $ 106", viewModel.monthlyPayment.value)
        assertEquals("Total cost : $ 12,727", viewModel.totalCost.value)
        assertEquals("Loan cost : $ 2,727", viewModel.interestCost.value)
    }
}