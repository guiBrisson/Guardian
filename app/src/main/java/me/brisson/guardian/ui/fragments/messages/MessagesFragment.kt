package me.brisson.guardian.ui.fragments.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentMessagesBinding
import me.brisson.guardian.ui.base.BaseFragment
import me.brisson.guardian.ui.fragments.appmessages.AppMessagesFragment
import me.brisson.guardian.ui.fragments.smsmessages.SmsMessageFragment

@AndroidEntryPoint
class MessagesFragment : BaseFragment() {

    companion object {
        fun newInstance() = MessagesFragment()
    }

    private val app = AppMessagesFragment.newInstance()
    private val sms = SmsMessageFragment.newInstance()

    private lateinit var binding: FragmentMessagesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)

        setUpTabLayout()


        return binding.root
    }

    private fun setUpTabLayout(){
        openFragment(app)

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
        val transaction = parentFragmentManager.beginTransaction()
        when (id){
            1 -> transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            2 -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        transaction.replace(R.id.messagesContainer, fragment)
        transaction.commit()
    }



}