package me.brisson.guardian.ui.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentLoginBinding
import me.brisson.guardian.ui.activities.forgotpassword.ForgotPasswordActivity
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseFragment
import me.brisson.guardian.ui.fragments.signup.SignUpFragment


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
        private val TAG = SignUpFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel = LoginViewModel()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        auth = Firebase.auth

        handleClickListener()

        return binding.root
    }

    private fun handleClickListener() {
        binding.enterButton.setOnClickListener {
            if (!checkingEditTextErrors()) {
                signInFirebase()
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            startActivity(ForgotPasswordActivity())
        }
    }

    private fun signInFirebase() {
        auth.signInWithEmailAndPassword(viewModel.email.value!!, viewModel.password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(MainActivity())
                } else {
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkingEditTextErrors(): Boolean{
        viewModel.emailError.value = viewModel.email.value.isNullOrEmpty()
        viewModel.passwordError.value = viewModel.password.value.isNullOrEmpty()

        if (viewModel.emailError.value!!) binding.emailTextField.error = getString(R.string.empty_field)
        else binding.emailTextField.error = null

        if (viewModel.passwordError.value!!) binding.passwordTextField.error = getString(R.string.empty_field)
        else binding.passwordTextField.error = null

        return viewModel.emailError.value!! ||
                viewModel.passwordError.value!!
    }


}