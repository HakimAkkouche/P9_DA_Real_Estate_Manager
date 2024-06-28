package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import java.util.Locale

/**
 * Adapter for displaying a list of realtors in a spinner.
 * Extends ArrayAdapter to provide custom views for the spinner.
 */
class RealtorAdapter(context: Context, private val realtorList: List<RealtorEntity>) :
    ArrayAdapter<RealtorEntity>(context, android.R.layout.simple_spinner_item, realtorList) {

    /**
     * Provides a view for the AdapterView (Spinner) when it is not selected.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val realtor = realtorList[position]
        val realtorString = "${realtor.title} ${realtor.lastname.uppercase(Locale.ROOT)} ${realtor.firstname}"
        view.findViewById<TextView>(android.R.id.text1).text = realtorString
        return view
    }

    /**
     * Provides a view for the drop-down portion of the AdapterView (Spinner).
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val realtor = realtorList[position]
        val realtorString = "${realtor.title} ${realtor.lastname.uppercase(Locale.ROOT)} ${realtor.firstname}"
        view.findViewById<TextView>(android.R.id.text1).text = realtorString
        return view
    }
}
