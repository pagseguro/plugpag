package br.com.uol.pagseguro.plugpagandroiddemo.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagandroiddemo.PlugPagManager;
import br.com.uol.pagseguro.plugpagandroiddemo.TaskHandler;
import br.com.uol.pagseguro.plugpagandroiddemo.helper.Bluetooth;

public class TerminalPaymentTask extends AsyncTask<PlugPagPaymentData, String, PlugPagTransactionResult> {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private TaskHandler mHandler = null;
    private PlugPagPaymentData mPaymentData = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new terminal payment task.
     *
     * @param handler Handler used to report updates.
     */
    public TerminalPaymentTask(@NonNull TaskHandler handler) {
        if (handler == null) {
            throw new RuntimeException("TaskHandler reference cannot be null");
        }

        this.mHandler = handler;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Task execution
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mHandler.onTaskStart();
    }

    @Override
    protected PlugPagTransactionResult doInBackground(PlugPagPaymentData... plugPagPaymentData) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;

        if (plugPagPaymentData != null && plugPagPaymentData.length > 0 && plugPagPaymentData[0] != null) {
            // Update throbber
            this.mPaymentData = plugPagPaymentData[0];
            this.publishProgress("");

            plugpag = PlugPagManager.getInstance().getPlugPag();

            try {
                plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getTerminal()));
                result = plugpag.doPayment(plugPagPaymentData[0]);
            } catch (Exception e) {
                this.publishProgress(e.getMessage());
            }

            this.mPaymentData = null;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0) {
            this.mHandler.onProgressPublished(values[0], this.mPaymentData);
        }
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);
        this.mHandler.onTaskFinished(plugPagTransactionResult);
        this.mHandler = null;
    }

}
