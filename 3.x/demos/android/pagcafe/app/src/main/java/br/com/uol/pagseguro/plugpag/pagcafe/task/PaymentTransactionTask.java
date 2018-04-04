package br.com.uol.pagseguro.plugpag.pagcafe.task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import br.com.uol.pagseguro.plugpag.pagcafe.exception.MissingDeviceInformationException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.plugpag.PlugPagManager;
import br.com.uol.pagseguro.plugpag.pagcafe.receipt.ReceiptManager;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagEventData;
import br.com.uol.pagseguro.plugpag.PlugPagEventListener;
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public class PaymentTransactionTask
        extends AsyncTask<PlugPagPaymentData, OnFragmentInteractionListener.FragmentInteractionInfo, PlugPagTransactionResult>
        implements PlugPagEventListener {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private OnPaymentExecutionListener mListener = null;
    private PlugPagDevice mDevice = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new task to perform a payment.
     *
     * @param listener Task's listener reference.
     * @param device   Device where the payment will be executed.
     */
    public PaymentTransactionTask(@Nullable OnPaymentExecutionListener listener,
                                  @NonNull PlugPagDevice device) {
        if (device == null) {
            throw new MissingDeviceInformationException("Cannot start payment without device information");
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
            this.mListener.onPrePayment(this.mDevice);
        }
    }

    @Override
    protected PlugPagTransactionResult doInBackground(PlugPagPaymentData... plugPagPaymentData) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;
        int initialization = PlugPag.RET_OK;

        if (plugPagPaymentData != null &&
                plugPagPaymentData.length > 0 &&
                plugPagPaymentData[0] != null) {
            plugpag = PlugPagManager.getInstance(null).getPlugPag();
            plugpag.setEventListener(this);
            result = plugpag.initBTConnection(this.mDevice);

            if (result != null && result.getResult() == PlugPag.RET_OK) {
                result = plugpag.doPayment(plugPagPaymentData[0]);

                if (result != null && result.getResult() == PlugPag.RET_OK) {
                    try {
                        ReceiptManager.getInstance(null).saveReceipt(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                result = new PlugPagTransactionResult(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        initialization);
            }
        }

        if (plugpag != null) {
            plugpag.setEventListener(null);
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(OnFragmentInteractionListener.FragmentInteractionInfo... values) {
        if (values != null && values.length > 0 && values[0] != null && this.mListener != null) {
            this.mListener.onUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);

        if (this.mListener != null) {
            this.mListener.onPostPayment(plugPagTransactionResult);
        }

        this.mDevice = null;
        this.mListener = null;
    }

    // ---------------------------------------------------------------------------------------------
    // PlugPag calback
    // ---------------------------------------------------------------------------------------------

    @Override
    public int onEvent(PlugPagEventData data) {
        Bundle bundle = null;
        OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo = null;
        String message = null;

        Log.i("PLUGPAG", String.format("Event code: %d", data.getEventCode()));
        message = PlugPagEventData.getDefaultMessage(data.getEventCode());

        if (message != null) {
            bundle = new Bundle();
            bundle.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_UPDATE_MESSAGE);
            bundle.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);
            interactionInfo = new OnFragmentInteractionListener.FragmentInteractionInfo(bundle);
            this.publishProgress(interactionInfo);
        }

        return PlugPag.RET_OK;
    }

    // ---------------------------------------------------------------------------------------------
    // Transaction execution listener interface
    // ---------------------------------------------------------------------------------------------

    /**
     * Payment task execution listener.
     */
    public interface OnPaymentExecutionListener {

        /**
         * Called before the task starts.
         *
         * @param device Device used to execute the payment.
         */
        void onPrePayment(@NonNull PlugPagDevice device);

        /**
         * Called when an update is reported by the PlugPag.
         *
         * @param interactionInfo Interaction info sent by the update request.
         */
        void onUpdate(OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo);

        /**
         * Called after the task finishes.
         *
         * @param result Payment transaction result.
         */
        void onPostPayment(PlugPagTransactionResult result);

    }

}
