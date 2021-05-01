package me.brisson.guardian.ui.activities.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityMainBinding
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.location.MapsFragment
import me.brisson.guardian.ui.fragments.myprofile.MyProfileFragment


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val location = MapsFragment.newInstance()
    private val profile = MyProfileFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel

        openFragment(location)
        bottomNavigationSetUp()

    }

    private fun bottomNavigationSetUp() {
        binding.bottomNavigationView.selectedItemId = R.id.location
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.location -> {
                    openFragment(location)
                    true
                }
                R.id.tools -> {
                    //todo
                    true
                }
                R.id.my_profile -> {
                    openFragment(profile)
                    true
                }
                else -> false
            }

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
           2 = Access Fine Location
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
            2 -> {
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationPermissionGranted = true
                    makeSnackBar(
                            binding.mainContainer,
                            "Permissão para localização garantida"
                    )
                }
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