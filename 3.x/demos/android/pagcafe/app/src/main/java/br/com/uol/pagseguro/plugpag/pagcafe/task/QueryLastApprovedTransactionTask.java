package br.com.uol.pagseguro.plugpag.pagcafe.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.uol.pagseguro.plugpag.pagcafe.exception.MissingDeviceInformationException;
import br.com.uol.pagseguro.plugpag.pagcafe.plugpag.PlugPagManager;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public class QueryLastApprovedTransactionTask extends AsyncTask<Void, Void, PlugPagTransactionResult> {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private PlugPagDevice mDevice = null;
    private OnQueryLastApprovedTransactionListener mListener = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new task to query for the last approved transaction.
     *
     * @param listener Task's listener reference.
     * @param device   Device that will be queried.
     */
    public QueryLastApprovedTransactionTask(@Nullable OnQueryLastApprovedTransactionListener listener,
                                            @NonNull PlugPagDevice device) {
        if (device == null) {
            throw new MissingDeviceInformationException("Cannot query approved payments without device information");
        }

        this.mListener = listener;
        this.mDevice = device;
    }

    // ---------------------------------------------------------------------------------------------
    // Task execution
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (this.mListener != null) {
            this.mListener.onPreQuery();
        }
    }

    @Override
    protected PlugPagTransactionResult doInBackground(Void... voids) {
        PlugPagTransactionResult transactionResult = null;
        PlugPag plugpag = null;
        int initialization = PlugPag.RET_OK;

        plugpag = PlugPagManager.getInstance().getPlugPag();
        initialization = plugpag.initBTConnection(this.mDevice);

        if (initialization == PlugPag.RET_OK) {
            transactionResult = plugpag.getLastApprovedTransaction();
        }

        return transactionResult;
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult result) {
        super.onPostExecute(result);

        if (this.mListener != null) {
            this.mListener.onPostQuery(result);
        }

        this.mListener = null;
        this.mDevice = null;
    }


    // ---------------------------------------------------------------------------------------------
    // Callback interface
    // ---------------------------------------------------------------------------------------------

    /**
     * Payment task execution listener.
     */
    public interface OnQueryLastApprovedTransactionListener {

        /**
         * Called before the task starts.
         */
        void onPreQuery();

        /**
         * Called after the task finishes.
         *
         * @param result Transaction result.
         */
        void onPostQuery(PlugPagTransactionResult result);

    }

}
