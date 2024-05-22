package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import java.util.Locale

class RealtorAdapter(context: Context, private val realtorList: List<RealtorEntity>) : ArrayAdapter<RealtorEntity>(context, android.R.layout.simple_spinner_item, realtorList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val realtor = realtorList[position]
        val realtorString = realtor.title+ " " + realtor.lastname.uppercase(Locale.ROOT) + " " + realtor.firstname
        view.findViewById<TextView>(android.R.id.text1).text = realtorString
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val realtor = realtorList[position]
        val realtorString = realtor.title+ " " + realtor.lastname.uppercase(Locale.ROOT) + " " + realtor.firstname
        view.findViewById<TextView>(android.R.id.text1).text = realtorString
        return view
    }
}