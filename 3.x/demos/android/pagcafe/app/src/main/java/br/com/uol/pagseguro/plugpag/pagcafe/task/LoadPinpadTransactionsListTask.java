package br.com.uol.pagseguro.plugpag.pagcafe.task;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.List;

import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.receipt.ReceiptManager;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public class LoadPinpadTransactionsListTask extends AsyncTask<Void, Void, List<PlugPagTransactionResult>> {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private LoadPinpadTransactionsListListener mListener = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new task to load a list of pinpad transactions.
     *
     * @param listener Loading events listener.
     */
    public LoadPinpadTransactionsListTask(@Nullable LoadPinpadTransactionsListListener listener) {
        this.mListener = listener;
    }

    // ---------------------------------------------------------------------------------------------
    // Task execution
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (this.mListener != null) {
            this.mListener.onPreLoadTransactionsList();
        }
    }

    @Override
    protected List<PlugPagTransactionResult> doInBackground(Void... voids) {
        List<PlugPagTransactionResult> list = null;
        String serialNumber = null;

        serialNumber = Bluetooth.getSelectedBluetoothDevice().getName().split("-")[1];
        list = ReceiptManager.getInstance(null).list(serialNumber);

        return list;
    }

    @Override
    protected void onPostExecute(List<PlugPagTransactionResult> transactions) {
        super.onPostExecute(transactions);

        if (this.mListener != null) {
            this.mListener.onPostLoadTransactionsList(transactions);
            this.mListener = null;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Callback interface
    // ---------------------------------------------------------------------------------------------

    /**
     * Listener for the task that loads pinpads transactions.
     */
    public interface LoadPinpadTransactionsListListener {

        /**
         * Called before the task to load the transactions list starts.
         */
        void onPreLoadTransactionsList();

        /**
         * Called after the task to load the transactions list finishes.
         *
         * @param transactions List of loaded transactions.
         */
        void onPostLoadTransactionsList(List<PlugPagTransactionResult> transactions);
    }

}
