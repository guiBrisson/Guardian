package me.brisson.guardian.ui.activities.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.brisson.guardian.R
import me.brisson.guardian.databinding.DialogReAuthBinding

class ReAuthDialog(val callback : (String) -> Unit): DialogFragment() {

    lateinit var binding : DialogReAuthBinding
    private lateinit var builder : AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogReAuthBinding.inflate(it.layoutInflater, null, false)

            builder = AlertDialog.Builder(binding.root.context)
            builder.setView(binding.root)

            binding.sendButton.setOnClickListener {
                if (!checkEmptyEditText()){
                    callback.invoke(binding.currentPasswordEditText.text.toString())
                    dialog?.dismiss()
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity can not be null")
    }

    private fun checkEmptyEditText() : Boolean{
        return if (binding.currentPasswordEditText.text.isNullOrEmpty()) {
            binding.currentPasswordTextField.error = getString(R.string.empty_field)
            true
        } else {
            binding.currentPasswordTextField.error = null
            false
        }
    }


}