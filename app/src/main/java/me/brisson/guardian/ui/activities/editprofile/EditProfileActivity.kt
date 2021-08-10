package me.brisson.guardian.ui.activities.editprofile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.ActivityEditProfileBinding
import me.brisson.guardian.ui.activities.dialogs.ReAuthDialog
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.R
import me.brisson.guardian.utils.ImageHelper


@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    companion object {
        private val TAG = EditProfileActivity::class.java.simpleName
    }

    private val viewModel = EditProfileViewModel()
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var imageHelper : ImageHelper

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
                        .fit()
                        .centerCrop()
                        .into(binding.userImageView)
                }

                vm.changePassword.observe(this, Observer {
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

                if (viewModel.name.value != user!!.displayName ||
                    viewModel.photo.value != user.photoUrl
                ) {
                    profileChangeRequest()
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

                viewModel.reAuthRequest.observe(this, Observer {
                    when (it) {
                        true -> {
                            callDialog()
                        }
                        false -> {
                            onBackPressed()
                        }
                        null -> {
                        }
                    }
                })

            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.changePhotoLayout.setOnClickListener {
            imageHelper.showDialogGalleryOrCamera()
        }
    }

    private fun callDialog() {
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

                        Log.w(TAG, e.message!!)
                    }
                }
            }
    }

    private fun changePhoneNumber() {
        //todo ???
    }

    private fun profileChangeRequest() {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(viewModel.name.value!!)
            .setPhotoUri(viewModel.photo.value)
            .build()
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.reAuthRequest.value = false
                    Log.d(TAG, "User name and photo updated.")
                } else {
                    Toast.makeText(
                        this,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_put, R.anim.exit_to_right)
    }

    //todo() make the request to change the user image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ImageHelper.GALLERY_REQUEST_CODE ||
            requestCode == ImageHelper.PHOTO_REQUEST_CODE)
            imageHelper.handleResult(requestCode, resultCode, data, object : ImageHelper.Callback{
                override fun onImageCompressed(image64: String?, imageBitmap: Bitmap?) {
//                viewModel.setImageThumb(image64)
                    binding.userImageView.setImageBitmap(imageBitmap)

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