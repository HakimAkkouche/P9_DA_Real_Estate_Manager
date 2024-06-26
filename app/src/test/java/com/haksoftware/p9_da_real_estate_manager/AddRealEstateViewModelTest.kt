package com.haksoftware.p9_da_real_estate_manager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.AddRealEstateViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.StateFlowTestUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class AddRealEstateViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var realEstateRepository: RealEstateRepository

    private lateinit var addRealEstateViewModel: AddRealEstateViewModel

    @Mock
    private lateinit var insertSuccessObserver: Observer<Boolean>
    @Mock
    private lateinit var photosObserver: Observer<List<PhotoEntity>>
    @Mock
    private lateinit var formValidObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        initViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        reset(realEstateRepository, insertSuccessObserver, photosObserver, formValidObserver)
    }

    private fun initViewModel() {
        addRealEstateViewModel = AddRealEstateViewModel(realEstateRepository)
    }

    @Test
    fun testInsertSuccess() = runTest {
        addRealEstateViewModel.insertSuccess.observeForever(insertSuccessObserver)

        // Définir les données nécessaires pour l'insertion
        addRealEstateViewModel.updatePrice("250000")
        addRealEstateViewModel.updateSurface("1200")
        addRealEstateViewModel.updateDescription("Beautiful spacious apartment")
        addRealEstateViewModel.updateAddress("123 Main St")
        addRealEstateViewModel.updateZipCode("12345")
        addRealEstateViewModel.updateCity("Anytown")
        addRealEstateViewModel.updateCountry("Anystate")
        addRealEstateViewModel.updateRoomCount(3)
        addRealEstateViewModel.updateBathroomCount(2)
        addRealEstateViewModel.updateIdRealtor(1)
        addRealEstateViewModel.updateIdType(1)

        // Mock l'insertion du bien immobilier
        `when`(realEstateRepository.insertRealEstate(any())).thenReturn(1L)
        `when`(realEstateRepository.insertPhotos(any())).thenReturn(Unit)
        `when`(realEstateRepository.insertIsNextToEntities(any())).thenReturn(Unit)

        addRealEstateViewModel.saveRealEstate(mock(Context::class.java))

        // Vérifier que l'observateur a été notifié de l'insertion réussie
        verify(insertSuccessObserver).onChanged(true)
    }

    @Test
    fun testAddPhotoEntity() = runTest {
        addRealEstateViewModel.photoEntityListLiveData.observeForever(photosObserver)

        val photo = PhotoEntity(
            idPhoto = 1,
            namePhoto = "front_view.jpg",
            descriptionPhoto = "Front view of the real estate",
            idRealEstate = 1
        )

        addRealEstateViewModel.addPhotoEntity(photo)

        val photosList = addRealEstateViewModel.photoEntityListLiveData.value
        assertTrue(photosList!!.contains(photo))
    }

    @Test
    fun testRemovePhotoEntity() = runTest {
        addRealEstateViewModel.photoEntityListLiveData.observeForever(photosObserver)

        val photo = PhotoEntity(
            idPhoto = 1,
            namePhoto = "front_view.jpg",
            descriptionPhoto = "Front view of the real estate",
            idRealEstate = 1
        )

        addRealEstateViewModel.addPhotoEntity(photo)
        addRealEstateViewModel.removePhotoEntity(photo)

        val photosList = addRealEstateViewModel.photoEntityListLiveData.value
        assertFalse(photosList!!.contains(photo))
    }

    @Test
    fun testIsFormValid() = runTest {

        // Vérifier que le formulaire est invalide au départ
        assertFalse(addRealEstateViewModel.isFormValid.value!!)

        // Remplir les champs nécessaires pour rendre le formulaire valide
        addRealEstateViewModel.updatePrice("250000")
        addRealEstateViewModel.updateSurface("1200")
        addRealEstateViewModel.updateDescription("Beautiful spacious apartment")
        addRealEstateViewModel.addPhotoEntity(
            PhotoEntity(
                idPhoto = 1,
                namePhoto = "front_view.jpg",
                descriptionPhoto = "Front view of the real estate",
                idRealEstate = 1
            )
        )

        // Vérifier que le formulaire est maintenant valide
        assertTrue(StateFlowTestUtils.getValueForTesting(addRealEstateViewModel.isFormValid))
    }
}
