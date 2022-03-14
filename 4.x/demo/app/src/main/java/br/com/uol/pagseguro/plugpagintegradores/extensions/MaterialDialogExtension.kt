package br.com.uol.pagseguro.plugpagintegradores.extensions

import android.content.Context
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import br.com.uol.pagseguro.plugpagintegradores.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.createMaterialDialog(
    block: MaterialAlertDialogBuilder.() -> Unit = {}
): AlertDialog {
    val builder = MaterialAlertDialogBuilder(this)
    builder.setPositiveButton(android.R.string.ok, null)
    block(builder)
    return builder.create()
}

fun Context.createProgressDialog(): AlertDialog {
    return createMaterialDialog {
        val padding =
            this@createProgressDialog.resources.getDimensionPixelOffset(R.dimen.layout_padding)
        val progressBar = ProgressBar(this@createProgressDialog)
        progressBar.setPadding(padding, padding, padding, padding)
        setView(progressBar)
        setPositiveButton(null, null)
        setCancelable(false)
    }
}
