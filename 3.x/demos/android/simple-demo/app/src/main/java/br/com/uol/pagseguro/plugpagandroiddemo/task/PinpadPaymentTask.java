package br.com.uol.pagseguro.plugpagandroiddemo.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagEventData;
import br.com.uol.pagseguro.plugpag.PlugPagEventListener;
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagandroiddemo.PlugPagManager;
import br.com.uol.pagseguro.plugpagandroiddemo.PreviousTransactions;
import br.com.uol.pagseguro.plugpagandroiddemo.TaskHandler;
import br.com.uol.pagseguro.plugpagandroiddemo.helper.Bluetooth;

public class PinpadPaymentTask
        extends AsyncTask<PlugPagPaymentData, String, PlugPagTransactionResult>
        implements PlugPagEventListener {

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
    public PinpadPaymentTask(@NonNull TaskHandler handler) {
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
            plugpag = PlugPagManager.getInstance().getPlugPag();
            plugpag.setEventListener(this);
            this.mPaymentData = plugPagPaymentData[0];

            try {
                // Update the throbber
                this.publishProgress("");

                // Perform payment
                plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getPinpad()));
                result = plugpag.doPayment(plugPagPaymentData[0]);
            } catch (Exception e) {
                this.publishProgress(e.getMessage());
            } finally {
                plugpag.setEventListener(null);
            }

            this.mPaymentData = null;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0 && values[0] != null) {
            this.mHandler.onProgressPublished(values[0], this.mPaymentData);
        }
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);

        if (plugPagTransactionResult != null &&
                !TextUtils.isEmpty(plugPagTransactionResult.getTransactionCode()) &&
                !TextUtils.isEmpty(plugPagTransactionResult.getTransactionId())) {
            PreviousTransactions.push(
                    new String[]{
                            plugPagTransactionResult.getTransactionCode(),
                            plugPagTransactionResult.getTransactionId()
                    });
        }

        this.mHandler.onTaskFinished(plugPagTransactionResult);
        this.mHandler = null;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // PlugPag event handling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public int onEvent(PlugPagEventData plugPagEventData) {
        this.publishProgress(PlugPagEventData.getDefaultMessage(plugPagEventData.getEventCode()));

        return PlugPag.RET_OK;
    }

}
