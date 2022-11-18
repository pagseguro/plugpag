package br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth

import android.bluetooth.BluetoothAdapter

class BluetoothUseCase : BluetoothUseCaseContract {

    override fun getPairedDevices() = BluetoothAdapter.getDefaultAdapter().bondedDevices.toList()

}