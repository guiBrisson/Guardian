package me.brisson.guardian.ui.activities.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityMainBinding
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel

        bottomNavigationSetUp()
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

                    true
                }
                else -> false
            }
        }
    }
}