package me.brisson.guardian.ui.activities.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityMainBinding
import me.brisson.guardian.ui.activities.firstscreen.FirstScreenActivity
import me.brisson.guardian.ui.activities.notifications.NotificationsActivity
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.messages.MessagesFragment


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val messages = MessagesFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel

        bottomNavigationSetUp()
        topAppBarSetUp()

    }

    private fun bottomNavigationSetUp() {
        binding.bottomNavigationView.selectedItemId = R.id.location
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

    private fun topAppBarSetUp() {
        binding.topAppBar.title = getString(R.string.location)

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.notifications -> {
                    startActivity(NotificationsActivity())
                    true
                }
                else -> false
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.profile -> {
                    Toast.makeText(this, menuItem.itemId, Toast.LENGTH_SHORT).show()
                }
                R.id.guardians -> {
                    Toast.makeText(this, menuItem.itemId, Toast.LENGTH_SHORT).show()
                }
                R.id.settings -> {
                    Toast.makeText(this, menuItem.itemId, Toast.LENGTH_SHORT).show()
                }
                R.id.leave -> {
                    startActivity(FirstScreenActivity())
                }
            }

            menuItem.isChecked = true
            binding.drawerLayout.closeDrawer(binding.navigationView)
            true
        }
    }

    private fun openFragment(fragment: Fragment, id: Int = 0) {
        val transaction = supportFragmentManager.beginTransaction()
        when (id) {
            1 -> transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            2 -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        transaction.replace(R.id.mainContainer, fragment)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /* REQUEST CODE MENU
           1 = Read contacts

           */
        when (requestCode) {
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeSnackBar(
                            binding.mainContainer,
                            getString(R.string.permission_read_contacts_granted)
                    )
                } else {
                    makeActionSnackBar(
                            binding.mainContainer,
                            getString(R.string.permission_read_contacts_denied),
                            getString(R.string.retry),
                            ::askForReadContactPermission)
                }
                return
            }
        }
    }

    private fun makeActionSnackBar(contextView: View, text: CharSequence, buttonText: CharSequence, clickFunc: () -> Unit) {
        val snackBar = Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(ContextCompat.getColor(applicationContext, R.color.rally_purple))
        snackBar.setAnchorView(binding.fab)
                .setAction(buttonText) {
                    clickFunc()
                }
                .show()

    }

    private fun makeSnackBar(contextView: View, text: CharSequence) {
        val snackBar = Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(ContextCompat.getColor(applicationContext, R.color.rally_purple))
        snackBar.setAnchorView(binding.fab)
                .show()
    }

    private fun askForReadContactPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
        )
    }
}