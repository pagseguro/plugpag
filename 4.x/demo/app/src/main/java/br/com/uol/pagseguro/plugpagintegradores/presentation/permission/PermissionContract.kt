package br.com.uol.pagseguro.plugpagintegradores.presentation.permission

import android.app.Activity

class PermissionContract {

    interface View {
        fun requestPermissions(permissions: Array<String>)
    }

    interface Presenter {
        fun bind(view: View)
        fun unbind()
        fun requestPermissions(activity: Activity, requestCode: Int)
    }


}