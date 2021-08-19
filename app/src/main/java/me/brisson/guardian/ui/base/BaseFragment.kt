package me.brisson.guardian.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import me.brisson.guardian.R

abstract class BaseFragment : Fragment() {

    private var dialog: Dialog? = null

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        val view = View.inflate(
            activity, R.layout.item_progress, null
        )

        dialog = Dialog(requireActivity(), R.style.Theme_AppCompat_Light)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(view)
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)

    }

    fun showDialog() {
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }

    fun startActivity(nextActivity: Activity, bundle: Bundle = Bundle(), flag: Int? = null ) {
        val it = Intent(activity, nextActivity::class.java)
        it.putExtra("", bundle)
        if (flag != null){
            it.flags = flag
        }
        startActivity(it)
    }
}