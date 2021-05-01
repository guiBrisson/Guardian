package me.brisson.guardian.ui.fragments.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentToolsBinding
import me.brisson.guardian.ui.base.BaseFragment

class ToolsFragment : BaseFragment() {

    companion object {
        fun newInstance() = ToolsFragment()
    }

    private lateinit var binding : FragmentToolsBinding
    private val viewModel = ToolsViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel


        return binding.root
    }



}