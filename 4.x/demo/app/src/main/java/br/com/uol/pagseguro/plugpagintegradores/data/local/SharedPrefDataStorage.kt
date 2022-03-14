package br.com.uol.pagseguro.plugpagintegradores.data.local

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import br.com.uol.pagseguro.plugpag.PlugPag
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary

class SharedPrefDataStorage(
    context: Context,
    val plugpag: PlugPag
) : DataStorageContract {

    companion object {
        const val SHARED_PREF_BLUETOOTH = "bluetooth"
        const val SHARED_PREF_TRANSACTIONS = "transactions"

        const val KEY_BLUETOOTH_DEVICE_NAME = "device-name"
        const val KEY_TRANSACTIONS_COUNT = "transactions-count"
        const val KEY_TRANSACTION_DATA_PREFIX = "transaction-id"

        const val SEPARATOR = ";"
    }

    private val bluetoothSharedPref =
        context.getSharedPreferences(SHARED_PREF_BLUETOOTH, Context.MODE_PRIVATE)

    private val transactionsSharedPref =
        context.getSharedPreferences(SHARED_PREF_TRANSACTIONS, Context.MODE_PRIVATE)

    private var serials: String? = null

    override fun saveSelectedBluetoothDevice(device: BluetoothDevice) =
        bluetoothSharedPref
            .edit()
            .putString(KEY_BLUETOOTH_DEVICE_NAME, device.name)
            .commit()

    override fun getSelectedBluetoothDevice(): String {
        try {
            if (serials == null)
                serials = plugpag.serialDevices ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return if (!serials.isNullOrEmpty())
            serials!!
        else
            bluetoothSharedPref.getString(KEY_BLUETOOTH_DEVICE_NAME, "")!!
    }

    override fun clearBluetoothPreferences() =
        bluetoothSharedPref
            .edit()
            .clear()
            .commit()

    override fun saveTransactionResult(result: PlugPagTransactionResult): Boolean {
        val tokens = mutableListOf(
            "${result.date}",
            "${result.time}",
            "${result.result}",
            "${result.errorCode}",
            "${result.transactionId}",
            "${result.transactionCode}",
            "${result.cardBrand}",
            "${result.amount}",
            "${result.paymentType}"
        )
        val count = getTransactionCount() + 1
        val transactionKey = getTransactionIdentifier(count)

        return transactionsSharedPref
            .edit()
            .putInt(KEY_TRANSACTIONS_COUNT, count)
            .putString(transactionKey, tokens.joinToString(SEPARATOR))
            .commit()
    }

    override fun getTransactionCount() = transactionsSharedPref.getInt(KEY_TRANSACTIONS_COUNT, 0)

    private fun getTransactionIdentifier(position: Int) = "$KEY_TRANSACTION_DATA_PREFIX-$position"

    override fun getTransaction(position: Int): TransactionSummary? {
        val identifier = getTransactionIdentifier(position)

        return if (transactionsSharedPref.contains(identifier)) {
            val content = transactionsSharedPref.getString(identifier, null)

            content?.let {
                val tokens = it.split(SEPARATOR)
                if (tokens.size > 8) {
                    // Transaction found and with enough data
                    TransactionSummary(
                        position,
                        tokens[0],
                        tokens[1],
                        tokens[4],
                        tokens[5],
                        tokens[6],
                        tokens[7],
                        tokens[8].toInt()
                    )
                } else {
                    // Transaction found but does not have enough data
                    null
                }
            }
        } else {
            null
        }
    }

    override fun deleteTransaction(position: Int): Boolean {
        val identifier = getTransactionIdentifier(position)

        return if (transactionsSharedPref.contains(identifier)) {
            transactionsSharedPref
                .edit()
                .remove(identifier)
                .commit()
        } else {
            false
        }
    }

}