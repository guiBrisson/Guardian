package me.brisson.guardian.ui.fragments.appmessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentAppMessagesBinding
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class AppMessagesFragment : BaseFragment() {

    companion object {
        fun newInstance() = AppMessagesFragment()
    }

    private lateinit var binding: FragmentAppMessagesBinding
    private val viewModel = AppMessagesViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppMessagesBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel



        return binding.root
    }


}