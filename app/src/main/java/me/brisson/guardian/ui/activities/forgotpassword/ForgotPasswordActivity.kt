package me.brisson.guardian.ui.activities.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityForgotPasswordBinding
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        binding.viewModel = viewModel

        clickListeners()

    }

    private fun clickListeners() {
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.sendButton.setOnClickListener {
            showValidationLayout()
        }
    }

    private fun showValidationLayout(){
        binding.validationLayout.visibility = View.VISIBLE
        binding.recoverPasswordLayout.visibility = View.GONE
    }
}