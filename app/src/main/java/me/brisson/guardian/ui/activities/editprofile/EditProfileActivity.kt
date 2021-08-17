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

    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.viewModel = viewModel
        imageHelper = ImageHelper(this)

        setupUI()
        handleClickListeners()
    }

    private fun setupUI() {
        if (user != null) {
            viewModel.let { vm ->
                vm.photo.value = user.photoUrl
                vm.name.value = user.displayName
                vm.email.value = user.email
                vm.phoneNumber.value = user.phoneNumber

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
            }


        }
    }

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

    private fun handleClickListeners() {
        binding.saveFAB.setOnClickListener {
            if (!checkTextInputErrors()) {

                if (viewModel.name.value != user!!.displayName) {
                    changeUserName()
                }

                if (viewModel.photoBitmap.value != null) {
                    handleImageUpload(viewModel.photoBitmap.value!!)
                }

                if (viewModel.phoneNumber.value != user.phoneNumber) {
                    changePhoneNumber()
                }

                if (viewModel.changePassword.value!!) {
                    changePassword()
                }

                if (viewModel.email.value != user.email) {
                    changeUserEmail()
                }

                viewModel.reAuthRequest.observe(this, {
                    when (it) {
                        true -> {
                            callReAuthDialog()
                        }
                        false -> {
                            onBackPressed()
                        }
                        null -> { }
                    }
                })

                if (viewModel.anyError.value!!) {
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

    private fun callReAuthDialog() {
        ReAuthDialog {
            if (it.isNotBlank()) {
                reAuth(it)
            }
        }.show(supportFragmentManager, "ReAuthDialog")
    }

    private fun changePassword() {
        user!!.updatePassword(viewModel.newPassword.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.reAuthRequest.value = false
                    Log.d(TAG, "User password updated.")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        viewModel.reAuthRequest.value = true
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.anyError.value = true
                        Log.w(TAG, e.message!!)
                    }
                }
            }
    }

    private fun changePhoneNumber() {
        //todo ???
    }

    private fun changeUserName() {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(viewModel.name.value!!)
            .build()
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.reAuthRequest.value = false
                    Log.d(TAG, "User name updated.")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        viewModel.reAuthRequest.value = true
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.anyError.value = true
                        Log.e(TAG, e.message!!)
                    }
                }
            }
    }

    private fun changeUserEmail() {
        user!!.updateEmail(viewModel.email.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.reAuthRequest.value = false
                    Log.d(TAG, "User email updated")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        viewModel.reAuthRequest.value = true
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.anyError.value = true
                        Log.e(TAG, e.message!!)
                    }

                }
            }
    }

    private fun reAuth(currentPassword: String) {
        val credential = EmailAuthProvider
            .getCredential(user!!.email!!, currentPassword)

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User re-authenticated.")
                } else {
                    Toast.makeText(
                        this,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w(TAG, "User re-authenticated failed.", task.exception)
                }
            }
    }

    private fun handleImageUpload(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val uid = user?.uid


        val reference = FirebaseStorage.getInstance().reference
            .child("profileImages")
            .child("$uid.jpeg")

        reference.putBytes(baos.toByteArray())
            .addOnSuccessListener {
                getDownloadUrl(reference)
            }
            .addOnFailureListener {
                Log.e(TAG, "onFailure: ", it.cause)
            }

    }

    private fun getDownloadUrl(reference: StorageReference) {
        reference.downloadUrl
            .addOnSuccessListener {
                Log.d(TAG, "getDownloadUrl Success: $it")
                setUserProfileUrl(it)
            }
    }

    private fun setUserProfileUrl(uri: Uri) {
        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        user!!.updateProfile(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "setUserProfileUrl: Successfully")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        viewModel.reAuthRequest.value = true
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.anyError.value = true
                        Log.e(TAG, e.message!!)
                    }
                }
            }

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