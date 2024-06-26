package com.haksoftware.realestatemanager.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt
object Utils {
    private const val euroValue = 0.93f

    /**
     * Converts a property price from Dollars to Euros.
     * NOTE: DO NOT DELETE, TO BE SHOWN DURING THE DEFENSE
     * @param dollars Amount in dollars to be converted to euros
     * @return Amount converted to euros
     */
    /*
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }
     */
    fun convertDollarToEuro(dollars: Float): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.FRANCE)
        return numberFormat.format((dollars * euroValue).roundToInt()) + " €"
    }

    /**
     * Converts square feet to square meters.
     * @param surface Surface area in square feet to be converted to square meters
     * @return Surface area in square meters
     */
    fun convertFtSquareToMSquare(surface: Float): Int {
        return (surface / 10.764f).roundToInt()
    }

    /**
     * Converts the current date to a more appropriate format.
     * NOTE: DO NOT DELETE, TO BE SHOWN DURING THE DEFENSE
     * @return Formatted date in "dd/MM/yyyy" format
     */
    /*
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }
     */
    fun getTodayDate(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    /**
     * Converts an epoch timestamp to a formatted date string.
     * @param dateLong Epoch timestamp to be converted
     * @return Formatted date in "dd/MM/yyyy" format
     */
    fun getEpochToFormattedDate(dateLong: Long): String {
        return LocalDateTime.ofEpochSecond(dateLong, 0, ZoneOffset.UTC).format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    /**
     * Checks the network connection.
     * NOTE: DO NOT DELETE, TO BE SHOWN DURING THE DEFENSE
     * //@param context Application context
     * @return Boolean indicating whether the Internet connection is available
     */
    /*
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }
     */
    suspend fun isInternetAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val address: InetAddress = InetAddress.getByName("www.google.com")
                !address.equals("")
            } catch (e: IOException) {
                false
            }
        }
    }

    /*
    fun isInternetAvailable(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return wifiManager.isWifiEnabled
    }
    */

    /**
     * Saves an image to internal storage.
     * @param context Application context
     * @param uri URI of the image to be saved
     * @param filename Name of the file to save the image as
     * @return Absolute path of the saved image
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri, filename: String): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val directory = File(context.filesDir, "images")
        if (!directory.exists()) {
            directory.mkdir()
        }
        val file = File(directory, filename)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
    }

    /**
     * Formats a number to US style.
     * @param number Number to be formatted
     * @return Formatted number in US style
     */
    fun formatNumberToUSStyle(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return "$ " + numberFormat.format(number)
    }

    /**
     * Checks if a given timestamp is within the last 7 days.
     * @param timestamp Epoch timestamp to be checked
     * @return Boolean indicating whether the timestamp is within the last 7 days
     */
    fun isDateWithinLast7Days(timestamp: Long): Boolean {
        val dateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC)
        val now = LocalDateTime.now()
        val daysDifference = ChronoUnit.DAYS.between(dateTime, now)
        return daysDifference < 7
    }
}
