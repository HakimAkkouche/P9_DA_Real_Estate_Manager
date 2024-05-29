package com.haksoftware.realestatemanager.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

object Utils {
    private const val euroValue = 0.93f
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars Montant en dollars à convertir en euros
     * @return Montant converti en euros
     */
    fun convertDollarToEuro(dollars: Float): Int {
        return (dollars * euroValue).roundToInt()
    }

    fun convertFtSquareToMSquare(surface: Float): Int {
        return (surface / 10.764f).roundToInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return Date formatée au format "yyyy/MM/dd"
     */
    fun getTodayDate(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context Contexte de l'application
     * @return Boolean indiquant si la connexion Internet est disponible
     */
    fun isInternetAvailable(): Boolean {
        return try {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            !address.equals("")
        } catch (e: IOException) {
            false
        }
    }/*
    fun isInternetAvailable(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return wifiManager.isWifiEnabled
    }*/
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
    fun formatNumberToUSStyle(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return "$" + numberFormat.format(number)
    }
}