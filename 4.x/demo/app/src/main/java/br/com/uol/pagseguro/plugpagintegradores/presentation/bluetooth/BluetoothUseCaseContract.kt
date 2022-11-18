package br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothUseCaseContract {

    fun getPairedDevices(): List<BluetoothDevice>

}