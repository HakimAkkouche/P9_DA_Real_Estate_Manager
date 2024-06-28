package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
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
/**
 * DialogFragment for adding photos. Allows the user to take a photo with the camera
 * or select an image from the gallery, and add a description to the photo.
 */
class AddPhotoDialog : DialogFragment() {

    private lateinit var listener: AddPhotoDialogListener
    private var selectedImageUri: Uri? = null
    private var _binding: DialogAddPhotoBinding? = null
    private lateinit var imageView: ImageView
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String

    // Launcher for requesting multiple permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> {
                    imagePickerIntent()
                }
                permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true -> {
                    takePhotoIntent()
                }
                else -> {
                    // Handle the case where permissions are not granted
                }
            }
        }

    // Launcher for picking an image from the gallery
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(selectedImageUri)
        }
    }

    // Launcher for taking a photo with the camera
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            val file = File(currentPhotoPath)
            selectedImageUri = Uri.fromFile(file)
            imageView.setImageURI(selectedImageUri)
        }
    }

    /**
     * Sets the listener for the dialog.
     *
     * @param listener The listener to handle photo addition events.
     */
    fun setListener(listener: AddPhotoDialogListener) {
        this.listener = listener
    }

    /**
     * Creates and returns the dialog for adding photos.
     */
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
            binding.submitButtonPhoto.setOnClickListener {
                if (binding.editDescPhoto.text.isNotEmpty()) {
                    if (selectedImageUri != null) {
                        val photoEntity = PhotoEntity(
                            0, selectedImageUri.toString(),
                            binding.editDescPhoto.text.toString(), 0
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

    /**
     * Checks if the required permissions are granted and opens the gallery if they are.
     */
    private fun checkPermissionsAndOpenGallery() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (arePermissionsGranted(permissions)) {
            imagePickerIntent()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    /**
     * Checks if the required permissions are granted and opens the camera if they are.
     */
    private fun checkPermissionsAndOpenCamera() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (arePermissionsGranted(permissions)) {
            takePhotoIntent()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    /**
     * Checks if all the specified permissions are granted.
     *
     * @param permissions The permissions to check.
     * @return True if all permissions are granted, false otherwise.
     */
    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Launches an intent to pick an image from the gallery.
     */
    private fun imagePickerIntent() {
        pickImageLauncher.launch("image/*")
    }

    /**
     * Launches an intent to take a photo with the camera.
     */
    private fun takePhotoIntent() {
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
            takePhotoLauncher.launch(photoURI)
        }
    }

    /**
     * Creates an image file to store the photo taken with the camera.
     *
     * @return The created image file.
     * @throws IOException If an error occurs while creating the file.
     */
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
}
