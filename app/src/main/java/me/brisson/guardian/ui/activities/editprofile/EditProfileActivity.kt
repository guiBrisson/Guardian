package me.brisson.guardian.ui.activities.editprofile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityEditProfileBinding
import me.brisson.guardian.ui.activities.dialogs.ReAuthDialog
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.signup.SignUpFragment

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    companion object {
        private val TAG = EditProfileActivity::class.java.simpleName
        private var dialogCalled = false
    }

    private val viewModel = EditProfileViewModel()
    private lateinit var binding: ActivityEditProfileBinding

    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        binding.viewModel = viewModel

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

                if (viewModel.changePassword.value!!) {
                    changePassword()
                    callDialog()
                }

                if (viewModel.name.value != user!!.displayName ||
                    viewModel.photo.value != user.photoUrl
                ) {
                    profileChangeRequest()
                }

                if (viewModel.email.value != user.email) {
                    changeUserEmail()
                    callDialog()
                }

                if (viewModel.phoneNumber.value != user.phoneNumber) {
                    changePhoneNumber()
                }

                if (dialogCalled) {
                    onBackPressed()
                } else return@setOnClickListener
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun callDialog() {
        if (!dialogCalled) {
            ReAuthDialog {
                dialogCalled = true
                reAuth(it)
            }.show(supportFragmentManager, "ReAuthDialog")
        }
    }

    private fun changePassword() {
        user!!.updatePassword(viewModel.newPassword.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                } else {
                    Log.w(TAG, "User password updated failed", task.exception)
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
                    Log.d(TAG, "User name and photo updated.")
                }
            }
    }

    private fun changeUserEmail() {
        user!!.updateEmail(viewModel.email.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email updated")
                } else {
                    Log.w(TAG, "User email updated failed", task.exception)
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
                    Log.w(TAG, "User re-authenticated failed.", task.exception)
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_put, R.anim.exit_to_right)
    }

}