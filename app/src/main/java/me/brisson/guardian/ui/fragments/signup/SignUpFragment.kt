package me.brisson.guardian.ui.fragments.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentSignUpBinding
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel = SignUpViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.enterButton.setOnClickListener {
            startActivity(MainActivity())
        }

        return binding.root
    }


}