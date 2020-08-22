package com.tulsivanol.coder.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.tulsivanol.coder.R


object Helper {
    fun showToast(str: String, con: Context) {
        Toast.makeText(con, str, Toast.LENGTH_LONG).show()
    }

    fun showDialog(context: Context): Dialog {
        val alertDialog = Dialog(context)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setCancelable(false)
        val factory = LayoutInflater.from(context)
        val view = factory.inflate(R.layout.transparent_dialog, null)
        alertDialog.setContentView(view)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return alertDialog
    }

    fun showAlertDialogWithoutBtn(context: Context, message: String?): Dialog {
        var builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(true)
        return builder.create()
    }


    fun showAlertDialogWithBtn(
        context: Context,
        message: String?,
        codeScanner: CodeScanner
    ): Dialog {
        var builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton("Confirm") { dialogInterface, i ->
            dialogInterface.dismiss()
            codeScanner.releaseResources()
            codeScanner.startPreview()
        }
        return builder.create()
    }
}
