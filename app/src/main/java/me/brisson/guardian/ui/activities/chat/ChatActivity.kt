package me.brisson.guardian.ui.activities.chat

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityChatBinding
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel = ChatViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.viewModel = viewModel

        setupUI()
    }

    // Setting up the user interface
    private fun setupUI() {
        // Change the color of navigation bar onCreate
        window.navigationBarColor = ContextCompat.getColor(this, R.color.color_surface)

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
    }
}