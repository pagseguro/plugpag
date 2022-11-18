package br.com.uol.pagseguro.plugpagintegradores.presentation.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AppPermissionsUseCase(
    private val context: Context,
    private val permissions: List<String>
) : AppPermissionsUseCaseContract {

    override fun getMissingPermissions() =
        permissions
            .filter { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            }
            .toTypedArray()

    override fun requestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

}