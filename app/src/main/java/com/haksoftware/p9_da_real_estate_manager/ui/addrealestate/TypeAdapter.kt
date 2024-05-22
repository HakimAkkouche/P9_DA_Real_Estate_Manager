package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity

class TypeAdapter(context: Context, private val typeList: List<TypeEntity>) : ArrayAdapter<TypeEntity>(context, android.R.layout.simple_spinner_item, typeList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val type = typeList[position]
        view.findViewById<TextView>(android.R.id.text1).text = type.nameType
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val type = typeList[position]
        view.findViewById<TextView>(android.R.id.text1).text = type.nameType
        return view
    }
}