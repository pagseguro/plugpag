package br.com.uol.pagseguro.plugpag.pagcafe.fragment.coffeepayment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import br.com.uol.pagseguro.plugpag.pagcafe.OnAbortListener;
import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.BluetoothDeviceType;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.InvalidContextTypeException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.plugpag.PlugPagManager;
import br.com.uol.pagseguro.plugpag.pagcafe.task.PaymentTransactionTask;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagEventData;
import br.com.uol.pagseguro.plugpag.PlugPagEventListener;
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;


public class CoffeePaymentFragment
        extends Fragment
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener,
        PaymentTransactionTask.OnPaymentExecutionListener, Bluetooth.BluetoothObservable,
        PlugPagEventListener {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    public static final String TAG = "CoffeePaymentFragment";
    public static final double COFFEE_VALUE = 2.50;
    public static final int MAX_COFFEE_AMOUNT = 9;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private TextView mTxtCoffeeInputValue = null;
    private TextView mTxtCoffeeTotalValue = null;
    private Button mBtnLessCoffee = null;
    private Button mBtnMoreCoffee = null;
    private SeekBar mBarAmount = null;
    private Button mBtnCredit = null;
    private Button mBtnDebit = null;
    private Button mBtnVoucher = null;
    private Button mBtnPay = null;
    private ViewGroup mContainerInstallments = null;
    private Button mBtnLessInstallments = null;
    private Button mBtnMoreInstallments = null;
    private TextView mTxtInstallments = null;

    private OnFragmentInteractionListener mInteractionListener = null;
    private OnAbortListener mAbortListener = null;
    private int mPaymentMethod = PlugPag.TYPE_CREDITO;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_coffee_payment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setupViews(view);
        this.updateInstallments(0);
    }

    @Override
    public void onAttach(Context context) {
        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new InvalidContextTypeException("Context reference must be of OnFragmentInteractionListener type");
        }

        super.onAttach(context);
        Bluetooth.register(this);
        this.mInteractionListener = (OnFragmentInteractionListener) context;

        if (context instanceof OnAbortListener) {
            this.mAbortListener = (OnAbortListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Bluetooth.unregister(this);
        this.mInteractionListener = null;
        this.mAbortListener = null;
    }

    // ---------------------------------------------------------------------------------------------
    // Views setup
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups the Views.
     *
     * @param view Root View.
     */
    private void setupViews(View view) {
        this.mTxtCoffeeInputValue = (TextView) view.findViewById(R.id.txt_coffee_input_value);
        this.mTxtCoffeeTotalValue = (TextView) view.findViewById(R.id.txt_coffee_total_value);
        this.mBtnLessCoffee = (Button) view.findViewById(R.id.btn_less_coffee);
        this.mBtnMoreCoffee = (Button) view.findViewById(R.id.btn_more_coffee);
        this.mBarAmount = (SeekBar) view.findViewById(R.id.bar_amount);
        this.mBtnCredit = (Button) view.findViewById(R.id.btn_credit);
        this.mBtnDebit = (Button) view.findViewById(R.id.btn_debit);
        this.mBtnVoucher = (Button) view.findViewById(R.id.btn_voucher);
        this.mBtnPay = (Button) view.findViewById(R.id.btn_pay);
        this.mContainerInstallments = (ViewGroup) view.findViewById(R.id.containerInstalments);
        this.mBtnLessInstallments = (Button) view.findViewById(R.id.btn_less_installment);
        this.mBtnMoreInstallments = (Button) view.findViewById(R.id.btn_more_installment);
        this.mTxtInstallments = (TextView) view.findViewById(R.id.txt_installments);

        this.mBtnLessCoffee.setOnClickListener(this);
        this.mBtnMoreCoffee.setOnClickListener(this);
        this.mBarAmount.setOnSeekBarChangeListener(this);
        this.mBtnCredit.setOnClickListener(this);
        this.mBtnDebit.setOnClickListener(this);
        this.mBtnVoucher.setOnClickListener(this);
        this.mBtnPay.setOnClickListener(this);
        this.mBtnLessInstallments.setOnClickListener(this);
        this.mBtnMoreInstallments.setOnClickListener(this);

        this.setupSeekbar();
        this.updateCoffeeValue();
        this.updateCoffeeAmountButtons();
        this.updatePaymentMethod(this.mPaymentMethod);
        this.updatePaymentButton();
    }

    /**
     * Setups the Seekbar.
     */
    private void setupSeekbar() {
        this.mBarAmount.setMax(CoffeePaymentFragment.MAX_COFFEE_AMOUNT);
    }

    /**
     * Returns the value to be used as the Seekbar's current value.
     *
     * @return Seekbar's current value.
     */
    private int getSeekbarProgress() {
        return this.mBarAmount.getProgress() + 1;
    }

    // ---------------------------------------------------------------------------------------------
    // Coffee value updates
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the total value to be calculated.
     *
     * @return Total value to be paid.
     */
    private double getTotalValue() {
        double totalValue = 0d;
        int progress = 0;

        progress = this.getSeekbarProgress();
        totalValue = CoffeePaymentFragment.COFFEE_VALUE * progress;

        return totalValue;
    }

    /**
     * Updates the coffee payment value.
     */
    private void updateCoffeeValue() {
        double totalValue = 0d;
        int progress = 0;

        // Calculate total value
        progress = this.getSeekbarProgress();
        totalValue = CoffeePaymentFragment.COFFEE_VALUE * progress;

        // Update display
        this.mTxtCoffeeInputValue.setText(
                String.format("R$ %.02f\nx %d",
                        CoffeePaymentFragment.COFFEE_VALUE,
                        progress));
        this.mTxtCoffeeTotalValue.setText(String.format("R$ %.02f", totalValue));
    }

    /**
     * Updates the less/more coffee Buttons availability.
     */
    private void updateCoffeeAmountButtons() {
        int amount = 0;

        amount = this.getSeekbarProgress();
        this.mBtnLessCoffee.setEnabled(amount > 1);
        this.mBtnMoreCoffee.setEnabled(amount <= CoffeePaymentFragment.MAX_COFFEE_AMOUNT);
    }

    // ---------------------------------------------------------------------------------------------
    // Seekbar changes
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.updateCoffeeValue();
        this.updateCoffeeAmountButtons();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing
    }

    // ---------------------------------------------------------------------------------------------
    // Payment method changes
    // ---------------------------------------------------------------------------------------------

    /**
     * Updates the payment method and its related Views.
     *
     * @param method Payment method to be set.
     */
    private void updatePaymentMethod(int method) {
        this.mPaymentMethod = method;
        this.mBtnCredit.setBackgroundResource(R.drawable.button_faded_background);
        this.mBtnDebit.setBackgroundResource(R.drawable.button_faded_background);
        this.mBtnVoucher.setBackgroundResource(R.drawable.button_faded_background);

        switch (method) {
            case PlugPag.TYPE_CREDITO:
                this.mContainerInstallments.setVisibility(View.VISIBLE);
                this.mBtnCredit.setBackgroundResource(R.drawable.button_background);
                break;

            case PlugPag.TYPE_DEBITO:
                this.mContainerInstallments.setVisibility(View.GONE);
                this.mBtnDebit.setBackgroundResource(R.drawable.button_background);
                break;

            case PlugPag.TYPE_VOUCHER:
                this.mContainerInstallments.setVisibility(View.GONE);
                this.mBtnVoucher.setBackgroundResource(R.drawable.button_background);
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Payment Button update
    // ---------------------------------------------------------------------------------------------

    /**
     * Updates the payment Button state.
     */
    private void updatePaymentButton() {
        boolean hasSelectedDevice = false;

        hasSelectedDevice = Bluetooth.getSelectedBluetoothDevice() != null;

        this.mBtnPay.setEnabled(hasSelectedDevice);

        if (hasSelectedDevice) {
            this.mBtnPay.setText(R.string.coffee_button_pay);
        } else {
            this.mBtnPay.setText(R.string.coffee_button_select_bluetooth_device);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Buttons click handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_less_coffee:
                this.mBarAmount.setProgress(this.mBarAmount.getProgress() - 1);
                break;

            case R.id.btn_more_coffee:
                this.mBarAmount.setProgress(this.mBarAmount.getProgress() + 1);
                break;

            case R.id.btn_credit:
                this.updatePaymentMethod(PlugPag.TYPE_CREDITO);
                break;

            case R.id.btn_debit:
                this.updatePaymentMethod(PlugPag.TYPE_DEBITO);
                break;

            case R.id.btn_voucher:
                this.updatePaymentMethod(PlugPag.TYPE_VOUCHER);
                break;

            case R.id.btn_pay:
                if (BluetoothDeviceType.isTerminal(Bluetooth.getSelectedBluetoothDevice()) ||
                        PlugPagManager.getInstance().getPlugPag().isAuthenticated()) {
                    this.startPayment();
                } else {
                    this.showMissingAuthenticationDialog();
                }

                break;

            case R.id.btn_less_installment:
                this.updateInstallments(-1);
                break;

            case R.id.btn_more_installment:
                this.updateInstallments(1);
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Installments update
    // ---------------------------------------------------------------------------------------------

    /**
     * Updates the installments amount.
     *
     * @param updateAmount Amount to be updated.
     */
    private void updateInstallments(int updateAmount) {
        int amount = 0;

        try {
            amount = this.getInstallments();
            amount += updateAmount;

            if (amount <= 1) {
                amount = 1;
                this.mBtnLessInstallments.setEnabled(false);
            } else {
                this.mBtnLessInstallments.setEnabled(true);
            }

            this.mTxtInstallments.setText(String.valueOf(amount));
        } catch (Exception e) {
            // Do nothing
        }
    }

    /**
     * Returns the amount of installments.
     *
     * @return Amount of installments.
     */
    private int getInstallments() {
        int installments = 1;

        try {
            installments = Integer.parseInt(this.mTxtInstallments.getText().toString());
        } catch (Exception e) {
            installments = 1;
        }

        return installments;
    }

    // ---------------------------------------------------------------------------------------------
    // Payment
    // ---------------------------------------------------------------------------------------------

    /**
     * Starts a payment task.
     */
    private void startPayment() {
        final PlugPagPaymentData paymentData;
        final PlugPagDevice device;
        final PlugPag plugpag;

        if (Bluetooth.getSelectedBluetoothDevice() == null) {
            Toast.makeText(this.getActivity(), R.string.msg_no_bluetooth_device_selected_warning, Toast.LENGTH_SHORT).show();
        } else {
            device = new PlugPagDevice(Bluetooth.getSelectedBluetoothDevice().getName());
            paymentData = this.createPaymentData();
            new PaymentTransactionTask(this, device).execute(paymentData);
        }
    }

    /**
     * Creates the payment data needed to start a payment.
     *
     * @return Payment data with information extracted from the user's input.
     */
    private PlugPagPaymentData createPaymentData() {
        PlugPagPaymentData data = null;
        PlugPagPaymentData.Builder builder = null;

        builder = new PlugPagPaymentData.Builder();
        builder.setType(this.mPaymentMethod);
        builder.setAmount((int) (this.getTotalValue() * 100));
        builder.setUserReference("PAGCAFE");

        if (this.mPaymentMethod == PlugPag.TYPE_CREDITO && this.getInstallments() > 1) {
            builder.setInstallmentType(PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR);
            builder.setInstallments(this.getInstallments());
        } else {
            builder.setInstallmentType(PlugPag.INSTALLMENT_TYPE_A_VISTA);
            builder.setInstallments(1);
        }

        data = builder.build();

        return data;
    }

    // ---------------------------------------------------------------------------------------------
    // Payment execution callbacks
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onPrePayment(@NonNull PlugPagDevice device) {
        if (this.mInteractionListener != null) {
            if (device != null && device.getType() == PlugPagDevice.TYPE_PINPAD) {
                FragmentInteraction.showProgressDialog(
                        this.mInteractionListener,
                        this.getString(R.string.title_waiting_for_payment),
                        this.getString(R.string.msg_wait),
                        this.mAbortListener);
            } else {
                FragmentInteraction.showProgressDialog(
                        this.mInteractionListener,
                        this.getString(R.string.title_waiting_for_payment),
                        this.getString(R.string.msg_wait));
            }
        }
    }

    @Override
    public void onUpdate(OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onInteract(interactionInfo);
        }
    }

    /**
     * Called after the payment task finishes.
     */
    @Override
    public void onPostPayment(PlugPagTransactionResult result) {
        if (this.mInteractionListener != null) {
            if (result == null) {
                // No result found, show error
                FragmentInteraction.showMessage(
                        this.mInteractionListener,
                        FragmentInteraction.DIALOG_TYPE_ERROR,
                        this.getString(R.string.title_error),
                        this.getString(R.string.msg_missing_result));
            } else {
                if (result.getResult() == PlugPag.RET_OK) {
                    // Show transaction result
                    this.showTransactionSuccessDialog(result);
                } else {
                    if (result.getResult() == PlugPag.MISSING_TOKEN) {
                        // Show missing token error
                        this.showMissingAuthenticationDialog();
                    } else {
                        // Show generic error
                        this.showErrorDialog(result);
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Result Dialogs
    // ---------------------------------------------------------------------------------------------

    /**
     * Prepares data to report a transaction success in a DialogFragment.
     *
     * @param result Payment transaction result.
     * @return Data used to report a transaction success.
     */
    private void showTransactionSuccessDialog(PlugPagTransactionResult result) {
        String message = null;

        message = result.getMessage();
        FragmentInteraction.showTransactionResult(
                this.mInteractionListener,
                this.getString(R.string.title_success),
                message,
                result);
    }

    private void showErrorDialog(PlugPagTransactionResult result) {
        Bundle data = null;
        String message = null;
        String errorCode = null;
        String resultMessage = null;

        // Prepare error message
        if (result.getErrorCode() != null) {
            errorCode = result.getErrorCode();
        } else {
            errorCode = "";
        }

        if (result.getMessage() != null) {
            resultMessage = result.getMessage();
        } else {
            resultMessage = "";
        }

        message = this.getString(
                R.string.msg_transaction_error,
                result.getResult(),
                errorCode,
                resultMessage);
        FragmentInteraction.showErrorMessage(
                this.mInteractionListener,
                this.getString(R.string.title_error),
                message);
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth selection handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onSetBluetoothDevice(@Nullable BluetoothDevice device) {
        this.updatePaymentButton();
    }

    // ---------------------------------------------------------------------------------------------
    // Transaction events handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public int onEvent(PlugPagEventData data) {
        Bundle bundle = null;

        bundle = new Bundle();
        bundle.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_PROGRESS_DIALOG);
        bundle.putBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, false);

        if (data != null) {
            bundle.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, String.format("%d", data.getEventCode()));
        } else {
            bundle.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, String.format("Evento sem mensagem: %d", System.currentTimeMillis()));
        }

        if (this.mInteractionListener != null) {
            this.mInteractionListener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(bundle));
        }

        return PlugPag.RET_OK;
    }

    // ---------------------------------------------------------------------------------------------
    // Missing authentication
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows a Dialog to warn the user that the authentication is missing.
     */
    private void showMissingAuthenticationDialog() {
        FragmentInteraction.showErrorMessage(this.mInteractionListener,
                this.getString(R.string.title_error),
                this.getString(R.string.msg_missing_authentication));
    }

}
