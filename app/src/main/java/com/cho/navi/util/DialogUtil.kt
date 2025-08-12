package com.cho.navi.util

import android.content.Context
import com.cho.navi.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtil {

    fun showLoginRequiredDialog(
        context: Context,
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_title_require_login))
            .setMessage(context.getString(R.string.dialog_message_require_login))
            .setPositiveButton(context.getString(R.string.dialog_positive_require_login))
            { dialog, which ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.dialog_negative_require_login), null)
            .show()
    }
}