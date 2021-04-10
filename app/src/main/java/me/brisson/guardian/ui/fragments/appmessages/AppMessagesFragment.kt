package me.brisson.guardian.ui.fragments.appmessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentAppMessagesBinding
import me.brisson.guardian.ui.adapters.MessageAdapter
import me.brisson.guardian.ui.base.BaseFragment
import me.brisson.guardian.utils.Status

@AndroidEntryPoint
class AppMessagesFragment : BaseFragment() {

    companion object {
        fun newInstance() = AppMessagesFragment()
    }

    private lateinit var binding: FragmentAppMessagesBinding
    private val viewModel = AppMessagesViewModel()
    lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppMessagesBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel


        setUpRecycler()


        return binding.root
    }

    private fun setUpRecycler() {
        adapter = MessageAdapter(arrayListOf())
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.getMessages().observe(viewLifecycleOwner, Observer {
            if (!it.data.isNullOrEmpty()) {

                when (it.status) {
                    Status.SUCCESS -> {
                        hideDialog()
                        adapter.addData(it.data)
                    }
                    Status.ERROR -> showDialog()
                    Status.LOADING -> showDialog()
                }
            }

            adapter.onItemClickListener = { }
        })
    }


}