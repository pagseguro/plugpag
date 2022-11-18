package br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothDevice

interface BluetoothContract {

    interface Model {
    }

    interface View {
        fun showPairedDevices(devices: List<BluetoothDevice>)
        fun showErrorLoadingPairedDevices()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()

        fun loadPairedDevices()

        fun saveSelectedBluetoothDevice(device: BluetoothDevice)
        fun getSelectedBluetoothDeviceName(activity: Activity): String
    }

}