package me.brisson.guardian.ui.fragments.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
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
    }

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel = SignUpViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setupUI()

        return binding.root
    }

    // Setting up users interface
    private fun setupUI() {
        //When clicked on the 'keyboard OK', perform the enterButton
        binding.passwordInputEditText.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.enterButton.performClick()
                    true
                }
                else -> false
            }
        }

        binding.enterButton.setOnClickListener {
            if (!checkingEditTextErrors()) {
                viewModel.firebaseAuth()
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            startActivity(ForgotPasswordActivity())
        }

        viewModel.getUserCreatedSuccessListener().observe(viewLifecycleOwner, { success ->
            if (success) {
                //Move to MainActivity
                startActivity(
                    MainActivity(),
                    flag = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                Toast.makeText(
                    requireContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Checking and handling errors on edit texts
    private fun checkingEditTextErrors(): Boolean {
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

        if (viewModel.nameError.value!!) binding.nameTextField.error =
            getString(R.string.empty_field)
        if (viewModel.emailError.value!!) binding.emailTextField.error =
            getString(R.string.empty_field)
        if (viewModel.passwordError.value!!) binding.passwordTextField.error =
            getString(R.string.empty_field)

        return viewModel.nameError.value!! ||
                viewModel.emailError.value!! ||
                viewModel.passwordError.value!!

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = viewModel.getAuth().currentUser
        if (currentUser != null) {
            startActivity(MainActivity())
        }
    }
}