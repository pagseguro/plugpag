package br.com.uol.pagseguro.plugpagintegradores.data.local

import android.bluetooth.BluetoothDevice
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary

interface DataStorageContract {

    fun saveSelectedBluetoothDevice(device: BluetoothDevice): Boolean
    fun getSelectedBluetoothDevice(): String
    fun clearBluetoothPreferences(): Boolean

    fun saveTransactionResult(result: PlugPagTransactionResult): Boolean
    fun getTransactionCount(): Int
    fun getTransaction(position: Int): TransactionSummary?
    fun deleteTransaction(position: Int): Boolean

}