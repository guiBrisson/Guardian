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
import me.brisson.guardian.utils.FragmentStateHelper
import me.brisson.guardian.ui.fragments.location.MapsFragment
import me.brisson.guardian.ui.fragments.myprofile.MyProfileFragment
import me.brisson.guardian.ui.fragments.tools.ToolsFragment


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val location = MapsFragment.newInstance()
    private val tools = ToolsFragment.newInstance()
    private val profile = MyProfileFragment.newInstance()

    private lateinit var stateHelper: FragmentStateHelper
    private val fragments = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        stateHelper = FragmentStateHelper(supportFragmentManager)
        binding.viewModel = viewModel

        if (savedInstanceState != null) {
            val helperState = savedInstanceState.getBundle(STATE_HELPER)
            stateHelper.restoreHelperState(helperState!!)
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.home
        }

        bottomNavigationSetUp()
        setupUI()

    }

    private fun setupUI() {
        binding.allowLocationButton.setOnClickListener {
            askForLocationPermission()
        }

        if (getLocationPermission()) {
            binding.allowLocationButton.visibility = View.GONE
            openFragment(location)
        } else {
            binding.allowLocationButton.visibility = View.VISIBLE
        }
    }

    private fun bottomNavigationSetUp() {
        binding.bottomNavigationView.selectedItemId = R.id.location
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.location -> {
                    if (getLocationPermission()) {
                        openFragment(location)

                        fragments[item.itemId] ?: location
                        stateHelper.restoreState(location, item.itemId)
                        saveCurrentState(item.itemId)

                        binding.allowLocationButton.visibility = View.GONE
                    } else {
                        binding.mainContainer.removeAllViews()
                        binding.allowLocationButton.visibility = View.VISIBLE
                    }
                    true
                }
                R.id.tools -> {
                    openFragment(tools)
                    binding.allowLocationButton.visibility = View.GONE

                    fragments[item.itemId] ?: tools
                    stateHelper.restoreState(tools, item.itemId)
                    saveCurrentState(item.itemId)
                    true
                }
                R.id.my_profile -> {
                    openFragment(profile)
                    binding.allowLocationButton.visibility = View.GONE

                    fragments[item.itemId] ?: profile
                    stateHelper.restoreState(profile, item.itemId)
                    saveCurrentState(item.itemId)
                    true
                }
                else -> false
            }

        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, fragment)
        transaction.commit()
    }

    private fun getLocationPermission(): Boolean {
        // Return true if location permission is granted, else false.
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            READ_CONTACT_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    makeSnackBar(
                        binding.mainContainer,
                        getString(R.string.permission_read_contacts_granted)
                    )
                } else {
                    makeActionSnackBar(
                        binding.mainContainer,
                        getString(R.string.permission_read_contacts_denied),
                        getString(R.string.retry),
                        ::askForReadContactPermission
                    )
                }
                return
            }
            ACCESS_FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    binding.bottomNavigationView.selectedItemId = R.id.home
                    binding.allowLocationButton.visibility = View.GONE
                    openFragment(location)

                    makeSnackBar(
                        binding.mainContainer,
                        getString(R.string.permission_fine_location_granted)
                    )
                } else {
                    makeActionSnackBar(
                        binding.mainContainer,
                        getString(R.string.permission_fine_location_denied),
                        getString(R.string.retry),
                        ::askForLocationPermission
                    )
                }
            }
        }
    }

    private fun makeActionSnackBar(
        contextView: View,
        text: CharSequence,
        buttonText: CharSequence,
        clickFunc: () -> Unit
    ) {
        Snackbar.make(contextView, text, Snackbar.LENGTH_LONG)
            .setActionTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.rally_purple
                )
            )
            .setAnchorView(binding.fab)
            .setAction(buttonText) { clickFunc() }
            .show()


    }

    private fun makeSnackBar(
        contextView: View,
        text: CharSequence
    ) {
        Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
            .setActionTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.rally_purple
                )
            )
            .setAnchorView(binding.fab)
            .show()
    }

    private fun askForLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            ACCESS_FINE_LOCATION_REQUEST_CODE
        )
    }

    private fun askForReadContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            1
        )
    }

    override fun onBackPressed() {
        // Don't send back to login
        if (binding.bottomNavigationView.selectedItemId != R.id.location) {
            binding.bottomNavigationView.selectedItemId = R.id.location
        } else {
            finishAffinity()
        }

    }

    private fun saveCurrentState(id: Int) {
        fragments[id]?.let { oldFragment ->
            stateHelper.saveState(oldFragment, id)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveCurrentState(binding.bottomNavigationView.selectedItemId)
        outState.putBundle(STATE_HELPER, stateHelper.saveHelperState())

        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val STATE_HELPER = "helper"

        private const val READ_CONTACT_REQUEST_CODE = 1
        private const val ACCESS_FINE_LOCATION_REQUEST_CODE = 2
    }
}

