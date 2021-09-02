package me.brisson.guardian.ui.activities.editprofile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.ActivityEditProfileBinding
import me.brisson.guardian.ui.activities.dialogs.ReAuthDialog
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.R
import me.brisson.guardian.utils.ImageHelper
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    companion object {
        private val TAG = EditProfileActivity::class.java.simpleName
    }

    private val viewModel = EditProfileViewModel()
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var imageHelper: ImageHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.viewModel = viewModel
        imageHelper = ImageHelper(this)

        setupUI()
        handleClickListeners()
    }

    // Setting up user interface
    private fun setupUI() {
        if (viewModel.getUser() != null) {
            viewModel.let { vm ->
                // Todo()
                vm.photo.value = vm.getUser()!!.photoUrl
                vm.name.value = vm.getUser()!!.displayName
                vm.email.value = vm.getUser()!!.email
                vm.phoneNumber.value = vm.getUser()!!.phoneNumber

                binding.nameEditText.addTextChangedListener {
                    vm.name.value = it.toString()
                }

                binding.emailEditText.addTextChangedListener {
                    vm.email.value = it.toString()
                }

                binding.phoneEditText.addTextChangedListener {
                    vm.phoneNumber.value = it.toString()
                }

                if (vm.photo.value != null) {
                    Picasso.get()
                        .load(vm.photo.value)
                        .resize(300, 300)
                        .centerCrop()
                        .into(binding.userImageView)
                }

                vm.changePassword.observe(this, {
                    if (it) {
                        binding.changePasswordLayout.visibility = View.VISIBLE
                    } else {
                        binding.changePasswordLayout.visibility = View.GONE
                    }
                })

                vm.getAnyException().observe(this, { exception ->
                    Toast.makeText(
                        this,
                        exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }


        }
    }

    // Checking and handling text input errors
    private fun checkTextInputErrors(): Boolean {
        viewModel.let {
            it.nameError.value = it.name.value.isNullOrBlank()
            it.emailError.value = it.email.value.isNullOrBlank()

            if (it.changePassword.value!!) {
                it.newPasswordError.value = it.newPassword.value.isNullOrBlank()

                if (it.newPasswordError.value!!) binding.newPasswordTextField.error =
                    getString(R.string.empty_field)
                else binding.newPasswordTextField.error = null
            } else {
                it.newPasswordError.value = false
            }

            if (it.nameError.value!!) binding.nameTextField.error = getString(R.string.empty_field)
            else binding.nameTextField.error = null

            if (it.emailError.value!!) binding.emailTextField.error =
                getString(R.string.empty_field)
            else binding.emailTextField.error = null

            return it.nameError.value!! ||
                    it.emailError.value!! ||
                    it.newPasswordError.value!!
        }

    }

    // Checking changes in users when save button is clicked
    private fun handleClickListeners() {
        binding.saveFAB.setOnClickListener {
            if (!checkTextInputErrors()) {

                if (viewModel.name.value != viewModel.getUser()!!.displayName) {
                    viewModel.changeUserName()
                }

                if (viewModel.photoBitmap.value != null) {
                    viewModel.handleImageUpload(viewModel.photoBitmap.value!!)
                }

                if (viewModel.phoneNumber.value != viewModel.getUser()!!.phoneNumber) {
                    viewModel.changePhoneNumber()
                }

                if (viewModel.changePassword.value!!) {
                    viewModel.changePassword()
                }

                if (viewModel.email.value != viewModel.getUser()!!.email) {
                    viewModel.changeUserEmail()
                }

                viewModel.getReAuthRequest().observe(this, {
                    when (it) {
                        true -> { callReAuthDialog() }
                        false -> { onBackPressed() }
                        null -> {  }
                    }
                })

                if (viewModel.getAnyError().value == true) {
                    Toast.makeText(this, "There was an error!", Toast.LENGTH_SHORT).show()
                }

                onBackPressed()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.changePhotoLayout.setOnClickListener {
            imageHelper.showDialogGalleryOrCamera()
        }
    }

    // Calling re auth dialog when needed
    private fun callReAuthDialog() {
        ReAuthDialog {
            if (it.isNotBlank()) {
                viewModel.reAuth(it)
            }
        }.show(supportFragmentManager, "ReAuthDialog")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_put, R.anim.exit_to_right)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Getting the image from the camera/gallery and loading on the screen and updating the viewModel
        if (requestCode == ImageHelper.GALLERY_REQUEST_CODE ||
            requestCode == ImageHelper.PHOTO_REQUEST_CODE
        )
            imageHelper.handleResult(requestCode, resultCode, data, object : ImageHelper.Callback {
                override fun onImageCompressed(imageURI: Uri?, imageBitmap: Bitmap?) {
                    viewModel.photo.value = imageURI

                    Picasso.get()
                        .load(imageURI)
                        .resize(300, 300)
                        .centerCrop()
                        .into(binding.userImageView)

                    viewModel.photoBitmap.value = imageBitmap

                    Log.d(TAG, "Image Success")
                }

                override fun onCanceled() {
                    Log.d(TAG, "Image Canceled")
                }

                override fun onError() {
                    Log.d(TAG, "Image Error")
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        imageHelper.onRequestPermissionsResult(requestCode, grantResults)
    }

}