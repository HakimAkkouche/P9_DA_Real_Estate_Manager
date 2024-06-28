package com.haksoftware.p9_da_real_estate_manager

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.haksoftware.realestatemanager.utils.Utils
import com.haksoftware.realestatemanager.utils.Utils.saveImageToInternalStorage
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class UtilsTest {

    private val testDispatcher = StandardTestDispatcher()
    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockContentResolver: ContentResolver

    @Mock
    private lateinit var mockUri: Uri

    private val filename = "testfile.jpg"
    private val directoryName = "images"
    private lateinit var directoryPath: String
    private lateinit var filesDir: File
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.contentResolver).thenReturn(mockContentResolver)
        val inputStream = ByteArrayInputStream("test data".toByteArray())
        `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(inputStream)

        // Setup files directory
        filesDir = File(System.getProperty("java.io.tmpdir"), "test_files")
        if (!filesDir.exists()) filesDir.mkdir()  // Ensure the base directory exists
        `when`(mockContext.filesDir).thenReturn(filesDir)
        directoryPath = File(filesDir, directoryName).absolutePath

        // Ensure the image directory exists
        val imageDirectory = File(filesDir, directoryName)
        if (!imageDirectory.exists()) {
            assert(imageDirectory.mkdirs())  // Use mkdirs() to create missing parent directories too
        }
        mockkStatic(InetAddress::class)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        unmockkStatic(InetAddress::class)
    }

    @Test
    fun convertDollarToEuro() {
        val result = Utils.convertDollarToEuro(100000f)
        val expectation = NumberFormat.getNumberInstance(Locale.FRANCE).format(93000) + " €"
        assertEquals(expectation, result)
    }

    @Test
    fun convertFtSquareToMSquare() {
        val result = Utils.convertFtSquareToMSquare(1000f)
        val expectation = "93 m²"
        assertEquals(expectation, result)
    }

    @Test
    fun getTodayDate() {
        val result = Utils.getTodayDate()
        val expectation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        assertEquals(expectation, result)
    }

    @Test
    fun getEpochToFormattedDate() {
        val epochDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val result = Utils.getEpochToFormattedDate(epochDate)
        val expectation = LocalDateTime.ofEpochSecond(epochDate,0, ZoneOffset.UTC).format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        assertEquals(expectation, result)
    }
    @ExperimentalCoroutinesApi
    @Test
    fun `test isInternetAvailable returns true when internet is available`() = runTest(testDispatcher) {
        every { InetAddress.getByName("www.google.com") } returns InetAddress.getByName("127.0.0.1")
        assertTrue(Utils.isInternetAvailable())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test isInternetAvailable returns false when internet is not available`() = runTest(testDispatcher) {
        every { InetAddress.getByName("www.google.com") } throws IOException()

        assertFalse(Utils.isInternetAvailable())
    }

    @Test
    fun `test saveImageToInternalStorage saves file correctly`() {
        val expectedFilePath = File(directoryPath, filename).absolutePath
        val filePath = saveImageToInternalStorage(mockContext, mockUri, filename)

        assertEquals(expectedFilePath, filePath)
        // Check if file exists and is not empty
        val savedFile = File(filePath)
        assert(savedFile.exists())
        assert(savedFile.length() > 0)
    }

    @Test
    fun formatNumberToUSStyle() {
        val result = Utils.formatNumberToUSStyle(100000)
        val expectation = "$ " + NumberFormat.getNumberInstance(Locale.US).format(100000)
        assertEquals(expectation, result)
    }

    @Test
    fun `isDateWithinLast7Days is true when less than 7 days` () {
        val date = LocalDateTime.now().minusDays(2)
        assertTrue(Utils.isDateWithinLast7Days(date.toEpochSecond(ZoneOffset.UTC)))
    }
    @Test
    fun `isDateWithinLast7Days is false when more than 7 days` () {
        val date = LocalDateTime.now().minusDays(8)
        assertFalse(Utils.isDateWithinLast7Days(date.toEpochSecond(ZoneOffset.UTC)))
    }
}