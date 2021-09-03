package me.brisson.guardian.ui.activities.chat

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.App
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.ActivityChatBinding
import me.brisson.guardian.ui.adapters.MessageAdapter
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel = ChatViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.viewModel = viewModel

        viewModel.setContact(intent.extras?.getSerializable("contact") as Contact)

        setupUI()

        setupRecycler()
    }

    // Setting up the user interface
    private fun setupUI() {
        // Change the color of navigation bar onCreate
        window.navigationBarColor = ContextCompat.getColor(this, R.color.color_surface)

        // Setting the title bar as contact name
        binding.toolBar.title = viewModel.getContact().value?.name

        binding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // When clicked on the 'keyboard SEND', perform the sendButton
        binding.editText.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener when (i) {
                EditorInfo.IME_ACTION_SEND -> {
                    binding.sendButton.performClick()
                    true
                }
                else -> false
            }
        }

        viewModel.getAnyException().observe(this, {
            if (it != null) {
                Toast.makeText(this, it.cause.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        binding.sendButton.setOnClickListener {
            viewModel.sendMessage()
            binding.editText.text.clear()
            binding.editText.clearFocus()
        }
    }

    // Setting up recycler view
    private fun setupRecycler() {
        val adapter = MessageAdapter(arrayListOf())
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.getMessages().observe(this, {
            if (!it.isNullOrEmpty()) {
                adapter.addData(it)
            }
        })
    }
}