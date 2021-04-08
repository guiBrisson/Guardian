package me.brisson.guardian.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentLoginBinding
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel = LoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.enterButton.setOnClickListener {
            startActivity(MainActivity())
        }

        return binding.root
    }


}