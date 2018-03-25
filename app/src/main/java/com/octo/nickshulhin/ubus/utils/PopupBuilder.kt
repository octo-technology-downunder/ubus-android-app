package com.octo.nickshulhin.ubus.utils

import android.R.string.cancel
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface



/**
 * Created by nickshulhin on 24/3/18.
 */
object PopupBuilder {
    fun showSingleButton(context: Context, message: String){
        val builder1 = AlertDialog.Builder(context)
        builder1.setMessage(message)
        builder1.setCancelable(true)

        builder1.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        val alert11 = builder1.create()
        alert11.show()
    }
}