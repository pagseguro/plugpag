package br.com.uol.pagseguro.plugpagandroiddemo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpag.PlugPagVoidData;
import br.com.uol.pagseguro.plugpagandroiddemo.helper.Generator;
import br.com.uol.pagseguro.plugpagandroiddemo.task.PinpadPaymentTask;
import br.com.uol.pagseguro.plugpagandroiddemo.task.PinpadVoidPaymentTask;
import br.com.uol.pagseguro.plugpagandroiddemo.task.TerminalPaymentTask;
import br.com.uol.pagseguro.plugpagandroiddemo.task.TerminalQueryTransactionTask;
import br.com.uol.pagseguro.plugpagandroiddemo.task.TerminalVoidPaymentTask;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, TaskHandler {

    // -----------------------------------------------------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------------------------------------------------

    private static final int PERMISSIONS_REQUEST_CODE = 0x1234;

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private AlertDialog mAlertDialog = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlugPagManager.create(this.getApplicationContext());
        this.setupEventListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PlugPag.REQUEST_CODE_AUTHENTICATION) {
            if (resultCode == PlugPag.RET_OK) {
                this.showMessage(R.string.msg_authentication_ok);
            } else {
                this.showMessage(R.string.msg_authentication_failed);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Views setup
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Setups event listeners for all Buttons.
     */
    private void setupEventListeners() {
        ViewGroup root = null;
        View currentView = null;

        root = this.getWindow().getDecorView().findViewById(android.R.id.content); // Default Android content container
        root = (ViewGroup) root.getChildAt(0); // ScrollView
        root = (ViewGroup) root.getChildAt(0); // LinearLayout

        for (int i = 0; i < root.getChildCount(); i++) {
            currentView = root.getChildAt(i);

            if (currentView instanceof Button) {
                currentView.setOnClickListener(this);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Click events
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_permissions:
                this.requestPermissions();
                break;

            case R.id.btn_authentication_check:
                this.checkAuthentication();
                break;

            case R.id.btn_authentication_request:
                this.requestAuthentication();
                break;

            case R.id.btn_authentication_invalidate:
                this.invalidateAuthentication();
                break;

            case R.id.btn_terminal_credit:
                this.startTerminalCreditPayment();
                break;

            case R.id.btn_terminal_credit_with_installments:
                this.startTerminalCreditWithInstallmentsPayment();
                break;

            case R.id.btn_terminal_debit:
                this.startTerminalDebitPayment();
                break;

            case R.id.btn_terminal_voucher:
                this.startTerminalVoucherPayment();
                break;

            case R.id.btn_terminal_void_payment:
                this.startTerminalVoidPayment();
                break;

            case R.id.btn_terminal_query:
                this.startTerminalQueryTransaction();
                break;

            case R.id.btn_pinpad_credit:
                this.startPinpadCreditPayment();
                break;

            case R.id.btn_pinpad_credit_with_installments:
                this.startPinpadCreditWithInstallmentsPayment();
                break;

            case R.id.btn_pinpad_debit:
                this.startPinpadDebitPayment();
                break;

            case R.id.btn_pinpad_voucher:
                this.startPinpadVoucherPayment();
                break;

            case R.id.btn_pinpad_void_payment:
                this.startPinpadVoidPayment();
                break;

        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Request missing permissions
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Requests permissions on runtime, if any needed permission is not granted.
     */
    private void requestPermissions() {
        String[] missingPermissions = null;

        missingPermissions = this.filterMissingPermissions(this.getManifestPermissions());

        if (missingPermissions != null && missingPermissions.length > 0) {
            ActivityCompat.requestPermissions(this, missingPermissions, MainActivity.PERMISSIONS_REQUEST_CODE);
        } else {
            this.showMessage(R.string.msg_all_permissions_granted);
        }
    }

    /**
     * Returns a list of permissions requested on the AndroidManifest.xml file.
     *
     * @return Permissions requested on the AndroidManifest.xml file.
     */
    private String[] getManifestPermissions() {
        String[] permissions = null;
        PackageInfo info = null;

        try {
            info = this.getPackageManager()
                    .getPackageInfo(this.getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            permissions = info.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PlugPag", "Package name not found", e);
        }

        if (permissions == null) {
            permissions = new String[0];
        }

        return permissions;
    }

    /**
     * Filters only the permissions still not granted.
     *
     * @param permissions List of permissions to be checked.
     * @return Permissions not granted.
     */
    private String[] filterMissingPermissions(String[] permissions) {
        String[] missingPermissions = null;
        List<String> list = null;

        list = new ArrayList<>();

        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    list.add(permission);
                }
            }
        }

        missingPermissions = list.toArray(new String[0]);

        return missingPermissions;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Authentication handling
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks if a user is authenticated.
     */
    private void checkAuthentication() {
        if (PlugPagManager.getInstance().getPlugPag().isAuthenticated()) {
            this.showMessage(R.string.msg_authentication_ok);
        } else {
            this.showMessage(R.string.msg_authentication_missing);
        }
    }

    /**
     * Requests authentication.
     */
    private void requestAuthentication() {
        PlugPagManager.getInstance().getPlugPag().requestAuthentication(this);
    }

    /**
     * Invalidates current authentication.
     */
    private void invalidateAuthentication() {
        PlugPagManager.getInstance().getPlugPag().invalidateAuthentication();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Terminal transactions
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts a new credit payment on a terminal.
     */
    private void startTerminalCreditPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_CREDITO)
                .setInstallmentType(PlugPag.INSTALLMENT_TYPE_A_VISTA)
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new TerminalPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new credit payment with installments on a terminal
     */
    private void startTerminalCreditWithInstallmentsPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_CREDITO)
                .setInstallmentType(PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR)
                .setInstallments(Generator.generateInstallments())
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new TerminalPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new debit payment on a terminal.
     */
    private void startTerminalDebitPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_DEBITO)
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new TerminalPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new voucher payment on a terminal.
     */
    private void startTerminalVoucherPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_VOUCHER)
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new TerminalPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new void payment on a terminal.
     */
    private void startTerminalVoidPayment() {
        new TerminalVoidPaymentTask(this).execute();
    }

    /**
     * Starts a new transaction query on a terminal.
     */
    private void startTerminalQueryTransaction() {
        new TerminalQueryTransactionTask(this).execute();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Pinpad transactions
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts a new credit payment on a pinpad.
     */
    private void startPinpadCreditPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_CREDITO)
                .setInstallmentType(PlugPag.INSTALLMENT_TYPE_A_VISTA)
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new PinpadPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new credit payment with installments on a pinpad.
     */
    private void startPinpadCreditWithInstallmentsPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_CREDITO)
                .setInstallmentType(PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR)
                .setInstallments(Generator.generateInstallments())
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new PinpadPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new debit payment on a pinpad.
     */
    private void startPinpadDebitPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_DEBITO)
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new PinpadPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a new voucher payment on a pinpad.
     */
    private void startPinpadVoucherPayment() {
        PlugPagPaymentData paymentData = null;

        paymentData = new PlugPagPaymentData.Builder()
                .setType(PlugPag.TYPE_VOUCHER)
                .setAmount(Generator.generateValue())
                .setUserReference(this.getString(R.string.plugpag_user_reference))
                .build();
        new PinpadPaymentTask(this).execute(paymentData);
    }

    /**
     * Starts a void payment on a pinpad.
     */
    private void startPinpadVoidPayment() {
        PlugPagVoidData voidData = null;
        String[] lastTransaction = null;

        lastTransaction = PreviousTransactions.pop();

        if (lastTransaction != null) {
            voidData = new PlugPagVoidData.Builder()
                    .setTransactionCode(lastTransaction[0])
                    .setTransactionId(lastTransaction[1])
                    .build();
            new PinpadVoidPaymentTask(this).execute(voidData);
        } else {
            this.showErrorMessage(R.string.msg_error_missing_transaction_data);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // AlertDialog
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Shows an AlertDialog with a simple message.
     *
     * @param message Message to be displayed.
     */
    private void showMessage(@Nullable String message) {
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
        }

        if (TextUtils.isEmpty(message)) {
            this.showErrorMessage(R.string.msg_error_unexpected);
        } else {
            this.mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(true)
                    .create();
            this.mAlertDialog.show();
        }
    }

    /**
     * Shows an AlertDialog with a simple message.
     *
     * @param message Resource ID of the message to be displayed.
     */
    private void showMessage(@StringRes int message) {
        String msg = null;

        msg = this.getString(message);
        this.showMessage(msg);
    }

    /**
     * Shows an AlertDialog with an error message.
     *
     * @param message Message to be displayed.
     */
    private void showErrorMessage(@Nullable String message) {
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
        }

        if (TextUtils.isEmpty(message)) {
            this.showErrorMessage(R.string.msg_error_unexpected);
        } else {
            this.mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_error)
                    .setMessage(message)
                    .setCancelable(true)
                    .create();
            this.mAlertDialog.show();
        }
    }

    /**
     * Shows an AlertDialog with an error message.
     *
     * @param message Resource ID of the message to be displayed.
     */
    private void showErrorMessage(@StringRes int message) {
        String msg = null;

        msg = this.getString(message);
        this.showErrorMessage(msg);
    }

    /**
     * Shows an AlertDialog with a ProgressBar.
     *
     * @param message Message to be displayed along-side with the ProgressBar.
     */
    private void showProgressDialog(@Nullable String message) {
        String msg = null;

        if (message == null) {
            msg = this.getString(R.string.msg_wait);
        } else {
            msg = message;
        }

        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            ((AppCompatTextView) this.mAlertDialog.findViewById(R.id.txt_message)).setText(msg);
        } else {
            this.mAlertDialog = new AlertDialog.Builder(this)
                    .setView(LayoutInflater.from(this).inflate(R.layout.progressbar, null))
                    .setCancelable(false)
                    .create();
            this.mAlertDialog.show();
            this.showProgressDialog(msg);
        }
    }

    /**
     * Shows an AlertDialog with a ProgressBar.
     *
     * @param message Resource ID of the message to be displayed along-side with the ProgressBar.
     */
    private void showProgressDialog(@StringRes int message) {
        String msg = null;

        msg = this.getString(message);
        this.showProgressDialog(msg);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Task handling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void onTaskStart() {
        this.showProgressDialog(R.string.msg_wait);
    }

    @Override
    public void onProgressPublished(String progress, Object transactionInfo) {
        String message = null;
        String type = null;

        if (TextUtils.isEmpty(progress)) {
            message = this.getString(R.string.msg_wait);
        } else {
            message = progress;
        }

        if (transactionInfo instanceof PlugPagPaymentData) {
            switch (((PlugPagPaymentData) transactionInfo).getType()) {
                case PlugPag.TYPE_CREDITO:
                    type = this.getString(R.string.type_credit);
                    break;

                case PlugPag.TYPE_DEBITO:
                    type = this.getString(R.string.type_debit);
                    break;

                case PlugPag.TYPE_VOUCHER:
                    type = this.getString(R.string.type_voucher);
                    break;
            }

            message = this.getString(
                    R.string.msg_payment_info,
                    type,
                    (double) ((PlugPagPaymentData) transactionInfo).getAmount() / 100d,
                    ((PlugPagPaymentData) transactionInfo).getInstallments(),
                    message);
        } else if (transactionInfo instanceof PlugPagVoidData) {
            message = this.getString(R.string.msg_void_payment_info, message);
        }

        this.showProgressDialog(message);
    }

    @Override
    public void onTaskFinished(Object result) {
        if (result instanceof PlugPagTransactionResult) {
            this.showResult((PlugPagTransactionResult) result);
        } else if (result instanceof String) {
            this.showMessage((String) result);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Result display
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Shows a transaction's result.
     *
     * @param result Result to be displayed.
     */
    private void showResult(@NonNull PlugPagTransactionResult result) {
        String resultDisplay = null;
        List<String> lines = null;

        if (result == null) {
            throw new RuntimeException("Transaction result cannot be null");
        }

        lines = new ArrayList<>();
        lines.add(this.getString(R.string.msg_result_result, result.getResult()));

        if (!TextUtils.isEmpty(result.getErrorCode())) {
            lines.add(this.getString(R.string.msg_result_error_code, result.getErrorCode()));
        }

        if (!TextUtils.isEmpty(result.getAmount())) {
            String value = null;

            value = String.format("%.2f",
                    Double.parseDouble(result.getAmount()) / 100d);
            lines.add(this.getString(R.string.msg_result_amount, value));
        }

        if (!TextUtils.isEmpty(result.getAvailableBalance())) {
            String value = null;

            value = String.format("%.2f",
                    Double.parseDouble(result.getAmount()) / 100d);
            lines.add(this.getString(R.string.msg_result_available_balance, value));
        }

        if (!TextUtils.isEmpty(result.getBin())) {
            lines.add(this.getString(R.string.msg_result_bin, result.getBin()));
        }

        if (!TextUtils.isEmpty(result.getCardBrand())) {
            lines.add(this.getString(R.string.msg_result_card_brand, result.getCardBrand()));
        }

        if (!TextUtils.isEmpty(result.getDate())) {
            lines.add(this.getString(R.string.msg_result_date, result.getDate()));
        }

        if (!TextUtils.isEmpty(result.getTime())) {
            lines.add(this.getString(R.string.msg_result_time, result.getTime()));
        }

        if (!TextUtils.isEmpty(result.getHolder())) {
            lines.add(this.getString(R.string.msg_result_holder, result.getHolder()));
        }

        if (!TextUtils.isEmpty(result.getHostNsu())) {
            lines.add(this.getString(R.string.msg_result_host_nsu, result.getHostNsu()));
        }

        if (!TextUtils.isEmpty(result.getMessage())) {
            lines.add(this.getString(R.string.msg_result_message, result.getMessage()));
        }

        if (!TextUtils.isEmpty(result.getTerminalSerialNumber())) {
            lines.add(this.getString(R.string.msg_result_serial_number, result.getTerminalSerialNumber()));
        }

        if (!TextUtils.isEmpty(result.getTransactionCode())) {
            lines.add(this.getString(R.string.msg_result_transaction_code, result.getTransactionCode()));
        }

        if (!TextUtils.isEmpty(result.getTransactionId())) {
            lines.add(this.getString(R.string.msg_result_transaction_id, result.getTransactionId()));
        }

        if (!TextUtils.isEmpty(result.getUserReference())) {
            lines.add(this.getString(R.string.msg_result_user_reference, result.getUserReference()));
        }

        resultDisplay = TextUtils.join("\n", lines);
        this.showMessage(resultDisplay);
    }

}
