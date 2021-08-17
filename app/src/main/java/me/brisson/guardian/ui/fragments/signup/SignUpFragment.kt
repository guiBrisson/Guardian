package me.brisson.guardian.ui.fragments.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentSignUpBinding
import me.brisson.guardian.ui.activities.forgotpassword.ForgotPasswordActivity
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {

    companion object {
        fun newInstance() = SignUpFragment()
        private val TAG = SignUpFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel = SignUpViewModel()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        auth = Firebase.auth

        handleClickListener()

        return binding.root
    }

    private fun handleClickListener() {
        //When clicked on the 'keyboard OK', perform the enterButton
        binding.passwordInputEditText.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener when (i){
                EditorInfo.IME_ACTION_DONE -> {
                    binding.enterButton.performClick()
                    true
                }
                else -> false
            }
        }

        binding.enterButton.setOnClickListener {
            if (!checkingEditTextErrors()) {
                firebaseAuth()
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            startActivity(ForgotPasswordActivity())
        }
    }

    private fun firebaseAuth(){
        auth.createUserWithEmailAndPassword(viewModel.email.value!!, viewModel.password.value!!)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    // Update user name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(viewModel.name.value!!)
                        .build()
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }

                    //Move to MainActivity
                    startActivity(MainActivity())

                } else {
                    // If sign in fails
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkingEditTextErrors() : Boolean{
        viewModel.nameError.value = viewModel.name.value.isNullOrEmpty()
        binding.nameInputEditText.doOnTextChanged { _, _, _, count ->
            viewModel.nameError.value = count > 0
            binding.nameTextField.error = null
        }

        viewModel.emailError.value = viewModel.email.value.isNullOrEmpty()
        binding.emailInputEditText.doOnTextChanged { _, _, _, count ->
            viewModel.emailError.value = count > 0
            binding.emailTextField.error = null
        }

        viewModel.passwordError.value = viewModel.password.value.isNullOrEmpty()
        binding.passwordInputEditText.doOnTextChanged { _, _, _, count ->
            viewModel.passwordError.value = count > 0
            binding.passwordTextField.error = null
        }

        if (viewModel.nameError.value!!) binding.nameTextField.error = getString(R.string.empty_field)
        if (viewModel.emailError.value!!) binding.emailTextField.error = getString(R.string.empty_field)
        if (viewModel.passwordError.value!!) binding.passwordTextField.error = getString(R.string.empty_field)

        return viewModel.nameError.value!! ||
            viewModel.emailError.value!! ||
            viewModel.passwordError.value!!

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(MainActivity())
        }
    }
}