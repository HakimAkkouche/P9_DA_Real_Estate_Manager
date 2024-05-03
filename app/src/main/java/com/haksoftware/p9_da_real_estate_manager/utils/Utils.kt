package com.haksoftware.realestatemanager.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars Montant en dollars à convertir en euros
     * @return Montant converti en euros
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.812).toInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return Date formatée au format "yyyy/MM/dd"
     */
    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        return dateFormat.format(Date())
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