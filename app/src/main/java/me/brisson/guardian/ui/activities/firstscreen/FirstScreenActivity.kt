package me.brisson.guardian.ui.activities.firstscreen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityFirstScreenBinding
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.login.LoginFragment
import me.brisson.guardian.ui.fragments.signup.SignUpFragment

@AndroidEntryPoint
class FirstScreenActivity : BaseActivity() {

    private lateinit var binding: ActivityFirstScreenBinding
    private val viewModel: FirstScreenViewModel by viewModels()

    private val login = LoginFragment.newInstance()
    private val signUp = SignUpFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_first_screen)

        binding.viewModel = viewModel



        openFragment(login)


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> openFragment(login, 1)
                    1 -> openFragment(signUp, 2)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    private fun openFragment(fragment: Fragment, id: Int = 0) {
        val transaction = supportFragmentManager.beginTransaction()
        when (id){
            1 -> transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            2 -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}