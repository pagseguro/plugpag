package br.com.uol.pagseguro.plugpag.pagcafe.transaction;

import android.support.annotation.NonNull;

import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public class TransactionData {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private String mBluetoothDeviceName = null;
    private PlugPagTransactionResult mTransactionResult = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new TransactionData.
     *
     * @param bluetoothDeviceName Bluetooth device that generated the transaction data.
     * @param result              Result of a transaction to be stored.
     */
    public TransactionData(@NonNull String bluetoothDeviceName, @NonNull PlugPagTransactionResult result) {
        this.mBluetoothDeviceName = bluetoothDeviceName;
        this.mTransactionResult = new PlugPagTransactionResult(result.getMessage(),
                result.getErrorCode(),
                result.getTransactionCode(),
                result.getDate(),
                result.getTime(),
                result.getHostNsu(),
                result.getCardBrand(),
                result.getBin(),
                result.getHolder(),
                result.getUserReference(),
                result.getTerminalSerialNumber(),
                result.getTransactionId(),
                result.getAmount(),
                result.getAvailableBalance(),
                result.getCardApplication(),
                result.getCardCryptogram(),
                result.getLabel(),
                result.getHolder(),
                result.getExtendedHolderName(),
                result.getResult());
    }

}
