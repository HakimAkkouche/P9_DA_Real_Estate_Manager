package com.haksoftware.p9_da_real_estate_manager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.haksoftware.p9_da_real_estate_manager.data.entity.*
import com.haksoftware.p9_da_real_estate_manager.data.repository.MapRepository
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.edit.GetRealEstateCallBack
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchCallback
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.RealEstatesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull

@ExperimentalCoroutinesApi
class RealEstatesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var realEstateRepository: RealEstateRepository
    @Mock
    private lateinit var mapRepository: MapRepository

    private lateinit var realEstatesViewModel: RealEstatesViewModel

    @Mock
    private lateinit var realEstateObserver: Observer<RealEstateWithDetails>
    @Mock
    private lateinit var realEstateObserverList: Observer<List<*>>
    @Mock
    private lateinit var updateSuccessObserver: Observer<Boolean>

    private lateinit var realEstateEntity: RealEstateEntity
    private lateinit var realtor: RealtorEntity
    private lateinit var photo: PhotoEntity
    private lateinit var listPhoto: MutableList<PhotoEntity>
    private lateinit var type: TypeEntity
    private lateinit var listPoi: MutableList<PointOfInterestEntity>
    private lateinit var isNextTo: IsNextToEntity
    private lateinit var realEstateWithDetails: RealEstateWithDetails
    private lateinit var modifiedRealEstateWithDetails: RealEstateWithDetails

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        initData()
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.updateSuccess.observeForever(updateSuccessObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        reset(realEstateRepository, mapRepository, updateSuccessObserver, realEstateObserver, realEstateObserverList)
    }

    @Test
    fun testGetAllRealEstates() = runTest {
        val reaEstatesListLiveData = MutableLiveData<List<RealEstateWithDetails>>()
        reaEstatesListLiveData.value = mutableListOf(realEstateWithDetails)
        `when`(realEstateRepository.getAllRealEstates()).thenReturn(reaEstatesListLiveData)

        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.realEstates.observeForever(realEstateObserverList)

        val realEstateList = realEstatesViewModel.realEstates.value
        assertTrue(realEstateList!!.isNotEmpty())
        assertEquals(realEstateWithDetails, realEstateList[0])
    }
    @Test
    fun testRealtorLiveData() = runTest {
        val expectedRealtors = listOf(
            RealtorEntity(idRealtor = 1, title = "Mr", lastname = "Doe", firstname = "John", email = "john.doe@example.com", phoneNumber = "1234567890")
        )
        val liveData = MutableLiveData<List<RealtorEntity>>()
        liveData.value = expectedRealtors

        `when`(realEstateRepository.getAllRealtor()).thenReturn(liveData)

        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.realtorLiveData.observeForever(realEstateObserverList)

        val realtors = realEstatesViewModel.realtorLiveData.value
        assertEquals(expectedRealtors, realtors)
    }
    @Test
    fun testTypesLiveData() = runTest {
        val expectedTypes = listOf(
            TypeEntity(idType = 1, nameType = "Apartment")
        )
        val liveData = MutableLiveData<List<TypeEntity>>()
        liveData.value = expectedTypes

        `when`(realEstateRepository.getAllTypes()).thenReturn(liveData)

        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.typesLiveData.observeForever(realEstateObserverList)

        val types = realEstatesViewModel.typesLiveData.value
        assertEquals(expectedTypes, types)
    }
    @Test
    fun testPOILiveData() = runTest {
        val expectedPOIs = listOf(
            PointOfInterestEntity(idPoi = 1, namePoi = "Park")
        )
        val liveData = MutableLiveData<List<PointOfInterestEntity>>()
        liveData.value = expectedPOIs

        `when`(realEstateRepository.getAllPointOfInterest()).thenReturn(liveData)

        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.pOILiveData.observeForever(realEstateObserverList)

        val pois = realEstatesViewModel.pOILiveData.value
        assertEquals(expectedPOIs, pois)
    }

    @Test
    fun testGetRealEstateWithDetails() = runTest {
        val realEstateId = 1
        val callback = mock(GetRealEstateCallBack::class.java)
        `when`(realEstateRepository.getRealEstate(realEstateId)).thenReturn(realEstateWithDetails)

        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.getRealEstateWithDetails(realEstateId, callback)

        verify(callback).onGetRealEstateResponse(realEstateWithDetails)
    }

    @Test
    fun testUpdateRealEstateWithDetails() = runTest {
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.currentRealEstate.observeForever(realEstateObserver)

        realEstatesViewModel.updateCurrentRealEstate(realEstateWithDetails)

        val currentRealEstate = realEstatesViewModel.currentRealEstate.value
        assertEquals(realEstateWithDetails, currentRealEstate)
    }

    @Test
    fun testSetOriginalRealEstateId() = runTest {
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.realEstateIdOriginal.observeForever(realEstateObserver)

        realEstatesViewModel.setOriginalRealEstateId(realEstateWithDetails.realEstate.idRealEstate)

        val realEstateWithDetails = realEstatesViewModel.realEstateIdOriginal.value
        assertEquals(realEstateWithDetails, realEstateWithDetails)
    }
    @Test
    fun testModifyRealEstateWithDetails() = runTest {
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.realEstateWithDetailsModified.observeForever(realEstateObserver)

        realEstatesViewModel.updateRealEstateWithDetails(realEstateWithDetails)

        val updatedValue = realEstatesViewModel.realEstateWithDetailsModified.value
        assertEquals(realEstateWithDetails, updatedValue)
    }

    @Test
    fun testPhotoToAddEntity() = runTest {
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.photoToAddEntity(photo)

        val photosList = realEstatesViewModel.photosToAddEntityList.first()
        assertEquals(1, photosList.size)
        assertEquals(photo, photosList[0])
    }

    @Test
    fun testPhotoToRemove() = runTest {
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.photoToRemove(photo)

        val photosList = realEstatesViewModel.photosToRemoveEntityList.first()
        assertEquals(1, photosList.size)
        assertEquals(photo, photosList[0])
    }

    @Test
    fun testUpdateIsNextTo() = runTest {
        val pointsOfInterest = listOf(1, 2, 3)
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.updatePointsOfInterest(pointsOfInterest)
        assertEquals(pointsOfInterest, realEstatesViewModel.isNextTo.first())
    }

    @Test
    fun testUpdateRealEstate() = runTest {
        realEstatesViewModel = RealEstatesViewModel(realEstateRepository, mapRepository)
        realEstatesViewModel.updateSuccess.observeForever(updateSuccessObserver)

        `when`(realEstateRepository.updateRealEstate(modifiedRealEstateWithDetails.realEstate)).thenReturn(Unit)

        // Définir les détails originaux et modifiés pour permettre la comparaison
        realEstatesViewModel.updateRealEstateWithDetails(modifiedRealEstateWithDetails)
        realEstatesViewModel.realEstateIdOriginal.value = realEstateWithDetails

        // Log pour vérifier les valeurs avant l'appel
        println("Original RealEstate: ${realEstatesViewModel.realEstateIdOriginal.value}")
        println("Modified RealEstate: ${realEstatesViewModel.realEstateWithDetailsModified.value}")

        // Appeler la fonction updateRealEstate
        realEstatesViewModel.updateRealEstate()

        // Vérifier que la méthode du dépôt a été appelée correctement
        verify(realEstateRepository).updateRealEstate(modifiedRealEstateWithDetails.realEstate)

        // Vérifier que l'observateur de updateSuccess a été notifié
        verify(updateSuccessObserver).onChanged(true)
    }
    private fun initData() {
        listPhoto = mutableListOf()
        listPoi = mutableListOf()
        photo = PhotoEntity(
            idPhoto = 1,
            namePhoto = "front_view.jpg",
            descriptionPhoto = "Front view of the real estate",
            idRealEstate = 1
        )
        realEstateEntity = RealEstateEntity(
            idRealEstate = 1,
            price = 250000,
            squareFeet = 1200,
            roomCount = 3,
            bathroomCount = 2,
            descriptionRealEstate = "Beautiful spacious apartment in downtown.",
            address = "123 Main St",
            postalCode = "12345",
            city = "Anytown",
            state = "Anystate",
            creationDate = System.currentTimeMillis(),
            soldDate = null,
            idRealtor = 1,
            idType = 1
        )
        realtor = RealtorEntity(idRealtor = 1, title = "Mr", lastname = "Doe", firstname = "John", email = "john.doe@example.com", phoneNumber = "1234567890")
        type = TypeEntity(idType = 1, nameType = "Apartment")
        listPhoto.add(PhotoEntity(idPhoto = 1, namePhoto = "front_view.jpg", descriptionPhoto = "Front view of the real estate", idRealEstate = 1))
        val poi = PointOfInterestEntity(idPoi = 1, namePoi = "Park")
        isNextTo = IsNextToEntity(idRealEstate = 1, idPoi = 1)
        listPoi.add(poi)
        realEstateWithDetails = RealEstateWithDetails(
            realEstate = realEstateEntity,
            realtor = realtor,
            type = type,
            photos = mutableListOf(photo),
            pointsOfInterest = listPoi,
            isNextToEntities = mutableListOf(isNextTo)
        )
        // Créer une version modifiée de realEstateWithDetails pour simuler une mise à jour
        modifiedRealEstateWithDetails = RealEstateWithDetails(
            realEstate = realEstateEntity.copy(price = 300000), // Modifier un champ pour simuler une mise à jour
            realtor = realtor,
            type = type,
            photos = mutableListOf(photo),
            pointsOfInterest = listPoi,
            isNextToEntities = mutableListOf(isNextTo)
        )
    }
}
