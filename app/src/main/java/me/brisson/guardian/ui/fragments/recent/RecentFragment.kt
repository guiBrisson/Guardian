package me.brisson.guardian.ui.fragments.recent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.brisson.guardian.databinding.FragmentRecentBinding

class RecentFragment : Fragment() {

    companion object {
        fun newInstance() = RecentFragment()
    }

    private lateinit var binding : FragmentRecentBinding
    private val viewModel = RecentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }



}