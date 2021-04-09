package me.brisson.guardian.ui.activities.notifications

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityNotificationsBinding
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.historic.HistoricFragment
import me.brisson.guardian.ui.fragments.recent.RecentFragment

@AndroidEntryPoint
class NotificationsActivity : BaseActivity() {

    private lateinit var binding : ActivityNotificationsBinding
    private val viewModel : NotificationsViewModel by viewModels()

    private val historic = HistoricFragment.newInstance()
    private val recent = RecentFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)

        binding.viewModel = viewModel

        layoutSetUp()

    }

    private fun layoutSetUp() {
        openFragment(recent)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> openFragment(recent, 1)
                    1 -> openFragment(historic, 2)
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
        transaction.replace(R.id.notificationContainer, fragment)
        transaction.commit()
    }

}