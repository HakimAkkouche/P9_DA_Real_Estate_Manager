package com.haksoftware.realestatemanager.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Utils {
    private val euroValue = 0.93f
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars Montant en dollars à convertir en euros
     * @return Montant converti en euros
     */
    fun convertDollarToEuro(dollars: Float): Float {
        return dollars * euroValue
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
    fun isInternetAvailable(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }
}