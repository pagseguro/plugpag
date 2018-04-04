package br.com.uol.pagseguro.plugpag.pagcafe.fragment.voidpayment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.BluetoothDeviceType;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.InvalidContextTypeException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.task.LoadPinpadTransactionsListTask;
import br.com.uol.pagseguro.plugpag.pagcafe.task.VoidPaymentTask;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpag.PlugPagVoidData;


public class VoidPaymentFragment
        extends Fragment
        implements VoidPaymentTask.OnVoidPaymentExecutionListener, View.OnClickListener,
                    Bluetooth.BluetoothObservable,
                    LoadPinpadTransactionsListTask.LoadPinpadTransactionsListListener,
                    AdapterView.OnItemClickListener {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    public static final String TAG = "VoidPaymentFragment";

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Button mBtnVoidPayment = null;
    private ViewGroup mContainerPinpad = null;
    private TextView mTxtDate = null;
    private TextView mTxtNoTransactionsFound = null;
    private ListView mLstPayments = null;

    private OnFragmentInteractionListener mInteractionListener = null;
    private List<PlugPagTransactionResult> mResults = null;
    private TransactionResultListAdapter mAdapter = null;

    // ---------------------------------------------------------------------------------------------
    // Fragment creation
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new VoidPaymentFragment instance.
     *
     * @return New VoidPaymentFragment instance.
     */
    public static final VoidPaymentFragment newInstance() {
        VoidPaymentFragment fragment = null;

        fragment = new VoidPaymentFragment();

        return fragment;
    }

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_void_payment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setupViews(view);

        // Auto start a void transaction
        if (BluetoothDeviceType.isTerminal(Bluetooth.getSelectedBluetoothDevice())) {
            this.mBtnVoidPayment.performClick();
        } else {
            this.startLoadTransactionsListTask();
        }
    }

    @Override
    public void onAttach(Context context) {
        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new InvalidContextTypeException("Context reference must be of OnFragmentInteractionListener type");
        }

        super.onAttach(context);
        this.mInteractionListener = (OnFragmentInteractionListener) context;

        Bluetooth.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mInteractionListener = null;
        Bluetooth.unregister(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Views setup
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups Views.
     *
     * @param view Root View.
     */
    private void setupViews(@NonNull View view) {
        // Set references
        this.mBtnVoidPayment = (Button) view.findViewById(R.id.btn_void_payment);
        this.mContainerPinpad = (ViewGroup) view.findViewById(R.id.container_pinpad);
        this.mTxtDate = (TextView) view.findViewById(R.id.txt_date);
        this.mTxtNoTransactionsFound = (TextView) view.findViewById(R.id.txt_no_transactions_found);
        this.mLstPayments = (ListView) view.findViewById(R.id.lst_payments);

        // Setup event listeners
        this.mBtnVoidPayment.setOnClickListener(this);
        this.mLstPayments.setOnItemClickListener(this);

        // Setup Views
        this.mTxtDate.setText(this.getString(R.string.void_transaction_date, this.getCurrentDateAsString()));

        this.updateViewsVisibility();

        // Set list adapter
        this.mResults = new ArrayList<>();
        this.mAdapter = new TransactionResultListAdapter(this.getContext(), -1, this.mResults);
        this.mLstPayments.setAdapter(this.mAdapter);
    }

    /**
     * Updates the Views visibility depending on the selected bluetooth device.
     */
    private void updateViewsVisibility() {
        if (BluetoothDeviceType.isTerminal(Bluetooth.getSelectedBluetoothDevice().getName())) {
            this.mBtnVoidPayment.setVisibility(View.VISIBLE);
            this.mContainerPinpad.setVisibility(View.GONE);
        } else {
            this.mBtnVoidPayment.setVisibility(View.GONE);
            this.mContainerPinpad.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Returns the current date as a String.
     *
     * @return Current date formatted as String.
     */
    private String getCurrentDateAsString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }

    // ---------------------------------------------------------------------------------------------
    // Void transaction task listener
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onPreVoidPayment() {
        OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo = null;
        Bundle data = null;

        if (this.mInteractionListener != null) {
            data = new Bundle();
            data.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_PROGRESS_DIALOG);
            data.putString(FragmentInteraction.KEY_DIALOG_TITLE, this.getString(R.string.title_waiting_for_void_payment));
            data.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, this.getString(R.string.msg_wait));
            data.putBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, false);

            interactionInfo = new OnFragmentInteractionListener.FragmentInteractionInfo(data);
            this.mInteractionListener.onInteract(interactionInfo);
        }
    }

    @Override
    public void onUpdate(OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onInteract(interactionInfo);
        }
    }

    @Override
    public void onPostVoidPayment(PlugPagTransactionResult result) {
        OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo = null;
        Bundle data = null;

        if (this.mInteractionListener != null) {
            if (result == null) {
                // No result found, dismiss current DialogFragments
                data = new Bundle();
                data.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_DISMISS_DIALOG);
            } else {
                if (result.getResult() == PlugPag.RET_OK) {
                    // Prepare successful transaction DialogFragment
                    data = this.prepareTransactionSuccessDialog(result);
                } else {
                    // Prepare transaction error DialogFragment
                    data = this.prepareTransactionErrorDialog(result);
                }
            }

            interactionInfo = new OnFragmentInteractionListener.FragmentInteractionInfo(data);
            this.mInteractionListener.onInteract(interactionInfo);
        }

        this.startLoadTransactionsListTask();
    }

    // ---------------------------------------------------------------------------------------------
    // Void payment result Dialog preparation
    // ---------------------------------------------------------------------------------------------

    /**
     * Prepares data to show a default DialogFragment.
     *
     * @return Data used to show a default DialogFragment.
     */
    private Bundle prepareTransactionResultDialog() {
        Bundle data = null;

        data = new Bundle();
        data.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_DIALOG);
        data.putInt(FragmentInteraction.KEY_DIALOG_TYPE, FragmentInteraction.DIALOG_TYPE_NORMAL);
        data.putBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, true);

        return data;
    }

    /**
     * Prepares data to report a transaction success in a DialogFragment.
     *
     * @param result Payment transaction result.
     * @return Data used to report a transaction success.
     */
    private Bundle prepareTransactionSuccessDialog(PlugPagTransactionResult result) {
        Bundle data = null;
        String message = null;

        // Prepare error message
        message = result.getMessage();

        // Prepare data to show the error Dialog
        data = new Bundle();
        data.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_TRANSACTION_RESULT_DIALOG);
        data.putInt(FragmentInteraction.KEY_DIALOG_TYPE, FragmentInteraction.DIALOG_TYPE_NORMAL);
        data.putString(FragmentInteraction.KEY_DIALOG_TITLE, this.getString(R.string.title_success));
        data.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);
        data.putSerializable(FragmentInteraction.KEY_DIALOG_TRANSACTION_RESULT, result);
        data.putBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, true);

        return data;
    }

    /**
     * Prepares data to report a transaction error in a DialogFragment.
     *
     * @param result Payment transaction result.
     * @return Data used to report a transaction error.
     */
    private Bundle prepareTransactionErrorDialog(PlugPagTransactionResult result) {
        Bundle data = null;
        String message = null;

        // Prepare error message
        message = this.getString(R.string.msg_transaction_error,
                result.getResult(),
                result.getErrorCode(),
                result.getMessage());

        // Prepare data to show the error Dialog
        data = this.prepareTransactionResultDialog();
        data.putInt(FragmentInteraction.KEY_DIALOG_TYPE, FragmentInteraction.DIALOG_TYPE_ERROR);
        data.putString(FragmentInteraction.KEY_DIALOG_TITLE, this.getString(R.string.title_error));
        data.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);

        return data;
    }

    /**
     * Prepares data to report a transaction error in a DialogFragment.
     *
     * @param message Message to be displayed.
     * @return Data used to report a transaction error.
     */
    private Bundle prepareTransactionErrorDialog(@NonNull String message) {
        Bundle data = null;

        // Prepare data to show the error Dialog
        data = this.prepareTransactionResultDialog();
        data.putInt(FragmentInteraction.KEY_DIALOG_TYPE, FragmentInteraction.DIALOG_TYPE_ERROR);
        data.putString(FragmentInteraction.KEY_DIALOG_TITLE, this.getString(R.string.title_error));
        data.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);

        return data;
    }

    // ---------------------------------------------------------------------------------------------
    // Button click handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_void_payment:
                this.startVoidPaymenTask();
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Transactions list item click handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlugPagTransactionResult result = null;
        PlugPagVoidData voidData = null;

        if (view != null && view.getTag() != null) {
            result = (PlugPagTransactionResult) view.getTag();
            voidData = new PlugPagVoidData.Builder()
                    .setTransactionCode(result.getTransactionCode())
                    .setTransactionId(result.getTransactionId())
                    .build();
            this.startVoidPaymentTask(voidData);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth changes handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onSetBluetoothDevice(@Nullable BluetoothDevice device) {
        this.updateViewsVisibility();

        if (BluetoothDeviceType.isPinpad(Bluetooth.getSelectedBluetoothDevice().getName())) {
            this.startLoadTransactionsListTask();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Start void payment transaction task
    // ---------------------------------------------------------------------------------------------

    /**
     * Starts a void payment task for a terminal.
     */
    private void startVoidPaymenTask() {
        this.startVoidPaymentTask(null);
    }

    /**
     * Starts a void payment task.
     *
     * @param data Data used to void the payment.
     */
    private void startVoidPaymentTask(PlugPagVoidData data) {
        Bundle args = null;
        PlugPagDevice device = null;

        if (data == null && BluetoothDeviceType.isPinpad(Bluetooth.getSelectedBluetoothDevice())) {
            args = this.prepareTransactionErrorDialog(this.getString(R.string.msg_void_transaction_missing_data));

            if (this.mInteractionListener != null) {
                this.mInteractionListener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
            }
        } else {
            device = new PlugPagDevice(Bluetooth.getSelectedBluetoothDevice().getName());
            new VoidPaymentTask(this, device).execute(data);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Transactions loading task
    // ---------------------------------------------------------------------------------------------

    /**
     * Starts a task to load a list of transactions.
     */
    private void startLoadTransactionsListTask() {
        new LoadPinpadTransactionsListTask(this).execute();
    }

    // ---------------------------------------------------------------------------------------------
    // Transactions loading task callbacks
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onPreLoadTransactionsList() {
        this.mLstPayments.setVisibility(View.GONE);
    }

    @Override
    public void onPostLoadTransactionsList(List<PlugPagTransactionResult> transactions) {
        ArrayList<PlugPagTransactionResult> sortedTransactions = null;

        this.mResults.clear();

        if (transactions != null && transactions.size() > 0) {
            sortedTransactions = new ArrayList<>(transactions);
            Collections.reverse(sortedTransactions);

            this.mLstPayments.setVisibility(View.VISIBLE);
            this.mTxtNoTransactionsFound.setVisibility(View.GONE);
            this.mResults.addAll(sortedTransactions);
        } else {
            this.mLstPayments.setVisibility(View.GONE);
            this.mTxtNoTransactionsFound.setVisibility(View.VISIBLE);
        }

        this.mAdapter.notifyDataSetChanged();
    }

}
