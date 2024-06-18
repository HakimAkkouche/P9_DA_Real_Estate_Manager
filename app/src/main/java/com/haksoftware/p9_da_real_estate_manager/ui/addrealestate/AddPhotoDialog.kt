package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.haksoftware.p9_da_real_estate_manager.BuildConfig
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.DialogAddPhotoBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddPhotoDialog : DialogFragment() {

    private val PICK_IMAGE_CODE = 101
    private val TAKE_PHOTO_CODE = 102
    private val PERMISSIONS_REQUEST_CODE = 103

    private lateinit var listener: AddPhotoDialogListener
    private var selectedImageUri: Uri? = null
    private var _binding: DialogAddPhotoBinding? = null
    private lateinit var imageView: ImageView
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String

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
                checkPermissionsAndOpenGallery()
            }
            binding.btnTakePhoto.setOnClickListener {
                checkPermissionsAndOpenCamera()
            }
            binding.cancelButton.setOnClickListener { dialog!!.dismiss() }
            binding.submitButton.setOnClickListener {
                if (binding.editText.text.isNotEmpty()) {
                    if (selectedImageUri != null) {
                        val photoEntity = PhotoEntity(
                            0, selectedImageUri.toString(),
                            binding.editText.text.toString(), 0
                        )
                        listener.onPhotoDialogAdded(photoEntity)
                        dialog!!.dismiss()
                    }
                } else {
                    binding.descriptionInputLayout.error = getString(R.string.error_msg_desc)
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun checkPermissionsAndOpenGallery() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (arePermissionsGranted(permissions)) {
            imagePickerIntent()
        } else {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun checkPermissionsAndOpenCamera() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (arePermissionsGranted(permissions)) {
            takePhotoIntent()
        } else {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun imagePickerIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_CODE)
    }

    private fun takePhotoIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider", // Use BuildConfig.APPLICATION_ID
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_CODE -> {
                    selectedImageUri = data?.data
                    imageView.setImageURI(selectedImageUri)
                }
                TAKE_PHOTO_CODE -> {
                    val file = File(currentPhotoPath)
                    selectedImageUri = Uri.fromFile(file)
                    imageView.setImageURI(selectedImageUri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                if (permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    imagePickerIntent()
                } else if (permissions.contains(Manifest.permission.CAMERA)) {
                    takePhotoIntent()
                }
            } else {
                // Permissions were denied, handle accordingly
            }
        }
    }
}