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
import me.brisson.guardian.ui.base.FragmentStateHelper
import me.brisson.guardian.ui.fragments.location.MapsFragment
import me.brisson.guardian.ui.fragments.myprofile.MyProfileFragment
import me.brisson.guardian.ui.fragments.tools.ToolsFragment


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        private const val STATE_HELPER = "helper"

        private const val READ_CONTACT_REQUEST_CODE = 1
        private const val ACCESS_FINE_LOCATION_REQUEST_CODE = 2
    }

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
            openFragment(location)
        }

        bottomNavigationSetUp()

    }

    private fun bottomNavigationSetUp() {
        binding.bottomNavigationView.selectedItemId = R.id.location
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.location -> {
                    openFragment(location)

                    fragments[item.itemId] ?: location
                    stateHelper.restoreState(location, item.itemId)
                    saveCurrentState(item.itemId)
                    true
                }
                R.id.tools -> {
                    openFragment(tools)

                    fragments[item.itemId] ?: tools
                    stateHelper.restoreState(tools, item.itemId)
                    saveCurrentState(item.itemId)
                    true
                }
                R.id.my_profile -> {
                    openFragment(profile)

                    fragments[item.itemId] ?: profile
                    stateHelper.restoreState(profile, item.itemId)
                    saveCurrentState(item.itemId)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            READ_CONTACT_REQUEST_CODE -> {
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
                        ::askForReadContactPermission
                    )
                }
                return
            }
            ACCESS_FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    makeSnackBar(
                        binding.mainContainer,
                        "Permissão para localização garantida"
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
        val snackBar = Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.rally_purple
            )
        )
        snackBar.setAnchorView(binding.fab)
            .setAction(buttonText) {
                clickFunc()
            }
            .show()

    }

    private fun makeSnackBar(
        contextView: View,
        text: CharSequence
    ) {
        val snackBar = Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.rally_purple
            )
        )
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
}