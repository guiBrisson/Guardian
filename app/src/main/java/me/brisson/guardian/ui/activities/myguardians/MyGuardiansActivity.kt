package me.brisson.guardian.ui.activities.myguardians

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityMyGuardiansBinding
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.appmessages.AppMessagesFragment
import me.brisson.guardian.ui.fragments.smsmessages.SmsMessageFragment

@AndroidEntryPoint
class MyGuardiansActivity : BaseActivity() {

    private lateinit var binding: ActivityMyGuardiansBinding
    private val viewModel: MyGuardiansViewModel by viewModels()

    private val app = AppMessagesFragment.newInstance()
    private val sms = SmsMessageFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_guardians)
        binding.viewModel = viewModel

        setUpTabLayout()

    }

    private fun setUpTabLayout(){
        openFragment(app)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position){
                    0 -> openFragment(app, 1)
                    1 -> openFragment(sms, 2)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }

        })
    }

    private fun openFragment(fragment: Fragment, id: Int = 0) {
        val transaction = supportFragmentManager.beginTransaction()
        when (id){
            1 -> transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            2 -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        transaction.replace(R.id.messagesContainer, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_put, R.anim.exit_to_right)
    }
}