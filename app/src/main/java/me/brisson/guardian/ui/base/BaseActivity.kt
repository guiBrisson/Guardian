package me.brisson.guardian.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import me.brisson.guardian.R

abstract class BaseActivity : AppCompatActivity() {

    private var dialog: Dialog? = null

    override fun onStart() {
        super.onStart()
        setDialog()

    }

    private fun setDialog() {
        val view = View.inflate(
            this, R.layout.item_progress, null
        )

        dialog = Dialog(this, R.style.Theme_AppCompat_Light)
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

    fun startActivity(activity: Activity, bundle: Bundle? = Bundle()) {
        val it = Intent(this, activity::class.java)
        it.putExtra("", bundle)
        startActivity(it)
    }


}