package br.com.lucas.pomodoroapp.helpers

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

object AlertDialogHelper {

    fun show(
        context: Context,
        @StringRes title: Int,
        @StringRes bodyMessage: Int,
        @StringRes positiveButtonMessage: Int,
        positiveButtonAction: (() -> Unit),
        @StringRes negativeButtonMessage: Int
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(bodyMessage)
        builder.setPositiveButton(
            positiveButtonMessage
        ) { dialog, _ ->
            positiveButtonAction()
            dialog.cancel()
        }
        builder.setNegativeButton(
            negativeButtonMessage
        )
        { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun show(
        context: Context,
        @StringRes title: Int,
        bodyMessage: String,
        @StringRes positiveButtonMessage: Int,
        positiveButtonAction: (() -> Unit),
        @StringRes negativeButtonMessage: Int
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(bodyMessage)
        builder.setPositiveButton(
            positiveButtonMessage
        ) { dialog, _ ->
            positiveButtonAction()
            dialog.cancel()
        }
        builder.setNegativeButton(
            negativeButtonMessage
        )
        { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}
