package me.brisson.guardian.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import me.brisson.guardian.R

object AlertHelper {

    private var alertDialog: AlertDialog? = null
    private var hasDialog: Boolean = false

    fun alertTwoButtonsDialog(
        context: Context,
        message: String,
        buttonPositiveText: String,
        buttonNegativeText: String,
        callbackButtonPositive: DialogInterface.OnClickListener?,
        callbackButtonNegative: DialogInterface.OnClickListener?
    ) {
        if (!hasDialog) {
            val dialogBuilder = AlertDialog.Builder(context, R.style.StackedAlertDialogStyle)
            alertDialog = dialogBuilder.create()
            alertDialog!!.apply {
                setButton(DialogInterface.BUTTON_POSITIVE, buttonPositiveText, callbackButtonPositive)
                setButton(DialogInterface.BUTTON_NEGATIVE, buttonNegativeText, callbackButtonNegative)
                setMessage(message)
                setOnDismissListener { hasDialog = false }
                show()

                hasDialog = true
            }
        }
    }
}