package me.brisson.guardian.ui.fragments.historic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentHistoricBinding
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class HistoricFragment : BaseFragment() {

    companion object {
        fun newInstance() = HistoricFragment()
    }

    private lateinit var binding: FragmentHistoricBinding
    private val viewModel = HistoricViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel


        return binding.root
    }



}