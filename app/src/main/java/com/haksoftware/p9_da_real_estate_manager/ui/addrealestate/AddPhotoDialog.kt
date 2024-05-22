package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.databinding.DialogAddPhotoBinding


class AddPhotoDialog : DialogFragment() {

    private val PICK_IMAGE_CODE = 101
    private lateinit var listener: AddPhotoDialogListener
    private var selectedImageUri: Uri? = null
    private var _binding: DialogAddPhotoBinding? = null
    private lateinit var imageView: ImageView
    private val binding get() = _binding!!

    fun setListener(listener: AddPhotoDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            _binding = DialogAddPhotoBinding.inflate(layoutInflater)
            builder.setView(binding.root)

            imageView = binding.imageView

            binding.btnSelectImage.setOnClickListener {
                imagePickerIntent()
            }
            binding.cancelButton.setOnClickListener { dialog!!.dismiss() }
            binding.submitButton.setOnClickListener {
                if(binding.editText.text.isNotEmpty() ){
                    if(selectedImageUri !=null) {
                        val photoViewState =
                            PhotoViewState(selectedImageUri, binding.editText.text.toString())
                        listener.onPhotoDialogAdded(photoViewState)
                        dialog!!.dismiss()
                    }
                }
                else {
                    binding.descriptionInputLayout.error = getString(R.string.error_msg_desc)
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun imagePickerIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
        }
    }
}

