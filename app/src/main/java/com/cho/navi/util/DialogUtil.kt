package com.cho.navi.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtil {

    fun showLoginRequiredDialog(
        context: Context,
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("로그인이 필요합니다.")
            .setMessage("이 기능을 사용하려면 먼저 로그인을 해야합니다. 로그인 화면으로 이동할까요?")
            .setPositiveButton("로그인") { dialog, which ->
                onConfirm()
            }
            .setNegativeButton("다음에", null)
            .show()
    }
}