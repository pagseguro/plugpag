package br.com.uol.pagseguro.plugpagandroiddemo.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagEventData;
import br.com.uol.pagseguro.plugpag.PlugPagEventListener;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpag.PlugPagVoidData;
import br.com.uol.pagseguro.plugpagandroiddemo.PlugPagManager;
import br.com.uol.pagseguro.plugpagandroiddemo.TaskHandler;
import br.com.uol.pagseguro.plugpagandroiddemo.helper.Bluetooth;

public class PinpadVoidPaymentTask
        extends AsyncTask<PlugPagVoidData, String, PlugPagTransactionResult>
        implements PlugPagEventListener {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private TaskHandler mHandler = null;
    private PlugPagVoidData mVoidPaymentData = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new void payment task.
     *
     * @param handler Handler used to report updates.
     */
    public PinpadVoidPaymentTask(@NonNull TaskHandler handler) {
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
    protected PlugPagTransactionResult doInBackground(PlugPagVoidData... voidData) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;

        if (voidData != null && voidData.length > 0 && voidData[0] != null) {
            this.mVoidPaymentData = voidData[0];
            plugpag = PlugPagManager.getInstance().getPlugPag();
            plugpag.setEventListener(this);

            try {
                // Update the throbber
                this.publishProgress("");

                // Perform void payment
                plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getPinpad()));
                result = plugpag.voidPayment(voidData[0]);
            } catch (Exception e) {
                this.publishProgress(e.getMessage());
            } finally {
                plugpag.setEventListener(null);
            }

            this.mVoidPaymentData = null;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0 && values[0] != null) {
            this.mHandler.onProgressPublished(values[0], this.mVoidPaymentData);
        }
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);
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
