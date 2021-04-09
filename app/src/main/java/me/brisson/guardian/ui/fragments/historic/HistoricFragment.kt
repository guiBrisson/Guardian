package me.brisson.guardian.ui.fragments.historic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentHistoricBinding
import me.brisson.guardian.ui.adapters.NotificationAdapter
import me.brisson.guardian.ui.base.BaseFragment
import me.brisson.guardian.utils.Status

@AndroidEntryPoint
class HistoricFragment : BaseFragment() {

    companion object {
        fun newInstance() = HistoricFragment()
    }

    private lateinit var binding: FragmentHistoricBinding
    private lateinit var adapter: NotificationAdapter
    private val viewModel = HistoricViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setUpRecycler()


        return binding.root
    }

    private fun setUpRecycler() {
        adapter = NotificationAdapter(arrayListOf())

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.getHistoricNotification().observe(viewLifecycleOwner, {
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