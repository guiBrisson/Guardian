package me.brisson.guardian.ui.activities.editprofile

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityEditProfileBinding
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    private val viewModel = EditProfileViewModel()
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        binding.viewModel = viewModel

        setupUI()
    }

    private fun setupUI(){
        viewModel.getChangePassword().observe(this, Observer {
            if (it){
                binding.changePasswordLayout.visibility = View.VISIBLE
            } else {
                binding.changePasswordLayout.visibility = View.GONE
            }
        })

        binding.saveFAB.setOnClickListener {
            onBackPressed()
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_put, R.anim.exit_to_right)
    }

}