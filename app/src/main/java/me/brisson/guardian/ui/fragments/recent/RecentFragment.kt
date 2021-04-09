package me.brisson.guardian.ui.fragments.recent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentRecentBinding
import me.brisson.guardian.ui.adapters.NotificationAdapter
import me.brisson.guardian.ui.base.BaseFragment
import me.brisson.guardian.utils.Status

@AndroidEntryPoint
class RecentFragment : BaseFragment() {

    companion object {
        fun newInstance() = RecentFragment()
    }

    private lateinit var binding: FragmentRecentBinding
    private lateinit var adapter: NotificationAdapter
    private val viewModel = RecentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setUpRecycler()

        return binding.root
    }

    private fun setUpRecycler() {
        adapter = NotificationAdapter(arrayListOf())

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.getRecentNotification().observe(viewLifecycleOwner, {
            if (!it.data.isNullOrEmpty()) {
                binding.noNotificationPlaceHolder.visibility = View.GONE
                when (it.status) {
                    Status.SUCCESS -> {
                        adapter.addData(it.data)
                        hideDialog()
                    }
                    Status.ERROR -> showDialog()
                    Status.LOADING -> showDialog()
                }

                adapter.onItemClickListener = { }
            } else {
                binding.noNotificationPlaceHolder.visibility = View.VISIBLE
            }

        })
    }


}
