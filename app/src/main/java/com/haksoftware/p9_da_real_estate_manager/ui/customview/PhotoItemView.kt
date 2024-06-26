package com.haksoftware.p9_da_real_estate_manager.ui.customview

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.haksoftware.p9_da_real_estate_manager.R

/**
 * Custom view for displaying a photo with description and optional remove button.
 *
 * This view includes an ImageView for displaying the photo, a TextView for the photo description,
 * and an ImageButton for optionally removing the photo.
 *
 * @property Context Context of the view.
 * @property AttributeSet AttributeSet for the view (optional).
 * @property Int Default style attribute for the view (optional).
 */
class PhotoItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // Views in the layout
    private val imageView: ImageView
    private val imageButtonRemove: ImageButton
    private val textViewDescription: TextView

    init {
        inflate(context, R.layout.photo_item_view, this)

        // Initialize views
        imageView = findViewById(R.id.show_imageView)
        imageButtonRemove = findViewById(R.id.image_button_remove)
        textViewDescription = findViewById(R.id.img_description)
    }

    /**
     * Sets the image URI for the ImageView.
     *
     * @param uri URI of the image.
     */
    fun setImageURI(uri: Uri) {
        imageView.setImageURI(uri)
    }

    /**
     * Sets the description text for the photo.
     *
     * @param description Description text to display.
     */
    fun setDescription(description: String) {
        textViewDescription.text = description
    }

    /**
     * Sets the visibility of the remove button.
     *
     * @param visible True to make the remove button visible, false to hide it.
     */
    fun setRemoveButtonVisibility(visible: Boolean) {
        imageButtonRemove.visibility = if (visible) VISIBLE else GONE
    }

    /**
     * Sets an OnClickListener for the remove button.
     *
     * @param listener Listener to be invoked when the remove button is clicked.
     */
    fun setOnRemoveButtonClickListener(listener: OnClickListener?) {
        imageButtonRemove.setOnClickListener(listener)
    }
}
