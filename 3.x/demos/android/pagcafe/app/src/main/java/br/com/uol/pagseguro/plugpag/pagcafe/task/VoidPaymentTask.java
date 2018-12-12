package br.com.uol.pagseguro.plugpag.pagcafe.task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.uol.pagseguro.plugpag.pagcafe.exception.MissingDeviceInformationException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.plugpag.PlugPagManager;
import br.com.uol.pagseguro.plugpag.pagcafe.receipt.ReceiptManager;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagEventData;
import br.com.uol.pagseguro.plugpag.PlugPagEventListener;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpag.PlugPagVoidData;

public class VoidPaymentTask
        extends AsyncTask<PlugPagVoidData, OnFragmentInteractionListener.FragmentInteractionInfo, PlugPagTransactionResult>
        implements PlugPagEventListener {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private VoidPaymentTask.OnVoidPaymentExecutionListener mListener = null;
    private PlugPagDevice mDevice = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new task to perform a payment void.
     *
     * @param listener Task's listener reference.
     * @param device   Device where the payment will be executed.
     */
    public VoidPaymentTask(@Nullable VoidPaymentTask.OnVoidPaymentExecutionListener listener,
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
            this.mListener.onPreVoidPayment();
        }
    }

    @Override
    protected void onProgressUpdate(OnFragmentInteractionListener.FragmentInteractionInfo... values) {
        if (values != null && values.length > 0 && values[0] != null && this.mListener != null) {
            this.mListener.onUpdate(values[0]);
        }
    }

    @Override
    protected PlugPagTransactionResult doInBackground(PlugPagVoidData... plugPagVoidData) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;
        int initialization = PlugPag.RET_OK;

        plugpag = PlugPagManager.getInstance(null).getPlugPag();
        plugpag.setEventListener(this);
        initialization = plugpag.initBTConnection(this.mDevice);

        if (initialization == PlugPag.RET_OK) {
            if (plugPagVoidData != null &&
                    plugPagVoidData.length > 0 &&
                    plugPagVoidData[0] != null) {
                result = plugpag.voidPayment(plugPagVoidData[0]);
            } else {
                result = plugpag.voidPayment();
            }

            if (result != null && result.getResult() == PlugPag.RET_OK) {
                ReceiptManager.getInstance(null).delete(plugPagVoidData[0]);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);

        if (this.mListener != null) {
            this.mListener.onPostVoidPayment(plugPagTransactionResult);
        }

        this.mDevice = null;
        this.mListener = null;
    }

    @Override
    public int onEvent(PlugPagEventData data) {
        Bundle bundle = null;
        OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo = null;
        String message = null;

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
    public interface OnVoidPaymentExecutionListener {

        /**
         * Called before the task starts.
         */
        void onPreVoidPayment();

        /**
         * Called when an update is reported by the PlugPag.
         *
         * @param interactionInfo Interaction info sent by the update request.
         */
        void onUpdate(OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo);

        /**
         * Called after the task finishes.
         *
         * @param result Void payment transaction result.
         */
        void onPostVoidPayment(PlugPagTransactionResult result);

    }

}
