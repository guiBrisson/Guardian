package me.brisson.guardian.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
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

    fun makeActionSnackBar(
        contextView: View,
        text: CharSequence,
        buttonText: CharSequence,
        clickFunc: () -> Unit
    ) : Snackbar {
        return Snackbar.make(contextView, text, Snackbar.LENGTH_LONG)
            .setActionTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.rally_purple
                )
            )
            .setAction(buttonText) { clickFunc() }


    }

    fun makeSnackBar(
        contextView: View,
        text: CharSequence
    ) : Snackbar {
        return Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
            .setActionTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.rally_purple
                )
            )
    }



}