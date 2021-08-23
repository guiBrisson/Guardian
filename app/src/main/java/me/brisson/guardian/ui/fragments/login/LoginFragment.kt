package me.brisson.guardian.ui.fragments.login

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
import me.brisson.guardian.databinding.FragmentLoginBinding
import me.brisson.guardian.ui.activities.forgotpassword.ForgotPasswordActivity
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseFragment


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel = LoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setupUI()

        return binding.root
    }

    // Setting up user interface
    private fun setupUI() {
        // When clicked on the 'keyboard OK', perform the enterButton
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
                viewModel.signInFirebase()
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            startActivity(ForgotPasswordActivity())
        }

        // Observing sign in success listener
        viewModel.getSignInWithEmailAndPasswordSuccessListener().observe(viewLifecycleOwner, { success ->
            if (success) {
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

        if (viewModel.emailError.value!!) binding.emailTextField.error =
            getString(R.string.empty_field)
        if (viewModel.passwordError.value!!) binding.passwordTextField.error =
            getString(R.string.empty_field)

        return viewModel.emailError.value!! ||
                viewModel.passwordError.value!!
    }

}