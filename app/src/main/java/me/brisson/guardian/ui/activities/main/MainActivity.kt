package me.brisson.guardian.ui.activities.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityMainBinding
import me.brisson.guardian.ui.activities.notifications.NotificationsActivity
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.messages.MessagesFragment


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    private val messages = MessagesFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel

        bottomNavigationSetUp()

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.notifications -> {
                    startActivity(NotificationsActivity())
                    true
                }
                else -> false
            }
        }
    }

    private fun bottomNavigationSetUp() {
        binding.bottomNavigationView.selectedItemId = R.id.location
        binding.topAppBar.title = getString(R.string.location)
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.location -> {
                    binding.topAppBar.title = item.title

                    true
                }
                R.id.tools -> {
                    binding.topAppBar.title = item.title

                    true
                }
                R.id.messages -> {
                    binding.topAppBar.title = item.title
                    openFragment(messages)
                    true
                }
                else -> false
            }

        }
    }

    private fun openFragment(fragment: Fragment, id: Int = 0) {
        val transaction = supportFragmentManager.beginTransaction()
        when (id){
            1 -> transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            2 -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        transaction.replace(R.id.mainContainer, fragment)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //TODO MAKE THIS TOAST AN SNACK BAR
                    Toast.makeText(this, "Permission denied to read your contacts", Toast.LENGTH_SHORT).show();
                }
                return
            }
        }
    }
}