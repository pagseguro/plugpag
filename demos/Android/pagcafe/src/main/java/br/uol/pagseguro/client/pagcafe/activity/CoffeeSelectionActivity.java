package br.uol.pagseguro.client.pagcafe.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.fragment.ResultFragment;
import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.pagcafe.model.Type_Transaction;
import br.uol.pagseguro.client.pagcafe.task.TransactionBindCallback;
import br.uol.pagseguro.client.pagcafe.task.TransactionErrorCallback;
import br.uol.pagseguro.client.pagcafe.task.TransactionSuccessCallback;
import br.uol.pagseguro.client.pagcafe.task.TransactionTask;
import br.uol.pagseguro.client.plugpag.PlugPag;


/**
 * <p>{@link AppCompatActivity} para exibir opções de pagamento.</p>
 *
 * <b>
 * <p>Essa <i>Activity</i> serve apenas como guia de implementação. Os controles das referências dos
 * componentes (<i>AsyncTask</i>s, <i>View</i>s, etc) devem ser implementados conforme a necessidade
 * da sua aplicação.</p>
 * </b>
 *
 * <p>Na parte superior são exibidos o valor total da venda e a quantidade de cafés sendo
 * vendidos. O valor total e a quantidade de cafés são atualizadas sempre que os botões "-" e
 * "+" são clicados.</p>
 *
 * <p>Os botões na parte inferior tem as seguintes funções:</p>
 *
 * <ul>
 *     <li><em>Crédito</em>: Seleciona o tipo de pagamento "Crédito".</li>
 *     <li><em>Débito</em>: Seleciona o tipo de pagamento "Débito".</li>
 *     <li><em>-</em>: Decrementa a quantidade de cafés sendo vendidos em 1 unidade. Esse botão
 *     é desabilitado quando a quantidade de cafés é igual ou inferior a 1 unidade.</li>
 *     <li><em>+</em>: Incrementa a quantidade e cafés sendo vendidos em 1 unidade.</li>
 *     <li><em>Pagar</em>: Inicia a transação de pagamento com os valores exibidos na parte
 *     superior da <i>Activity</i>.</li>
 * </ul>
 *
 * <p>{@link #onCreate}: Cria referências para as Views, define seus event listeners e define
 * a quantidade inicial de cafés sendo vendidos.</p>
 *
 * <p>{@link #onClick}: Decide que operações devem ser executadas baseando-se no ID da View que gerou
 * o evento.</p>
 *
 * <ul>
 * <li><em>Crédito</em>: Define o tipo de pagamento para "Crédito". (A aparência do botão é
 * modificada automaticamente de acordo com seu <i>resource</i> definido na propriedade
 * <i>android:background</i>.</li>
 *
 * <li><em>Débito</em>: Define o tipo de pagamento para "Débito". (A aparência do botão é
 * modificada automaticamente de acordo com seu <i>resource</i> definido na propriedade
 * <i>android:background</i>.</li>
 *
 * <li><em>-</em>: Decrementa a quantidade de cafés sendo vendidos em 1 unidade e atualiza o preço
 * total e a quantidade de cafés exibidos. Se necessário, desabilita o botão para evitar quantidades
 * menores do que 1 unidade.</li>
 *
 * <li><em>+</em>: Incrementa a quantidade de cafés sendo vendidos em 1 unidade e atualiza o preço
 * total e a quantidade de cafés exibidos.</li>
 *
 * <li><em>Pagar</em>: Define o contador de tentativas com 0 (nova transação solicitada) e inicia
 * o processo de pagamento, desabilitando os botões enquanto o processo não for finalizado.</li>
 * </ul>
 *
 * <p>Os métodos {@link #onSuccess} e {@link #onError} são chamados quando as operações executadas pela
 * <i>TransactionTask</i> finalizarem, respectivamente, com sucesso ou com erro.</p>
 *
 * <ul>
 * <li>{@link #onSuccess}: Reabilita os botões da <i>Activity</i> e exibe o <i>DialogFragment</i>
 * com o resultado da operação.</li>
 *
 * <li>{@link #onError}: Se necessário, efetua retentativas de transação. Se não for necessário fazer
 * retentativas, exibe o <i>DialogFragment</i> com o resultado obtido na operação. A necessidade
 * de realizar retentativas é decidida pelo método {@link #shouldRetryTransaction}</li>
 * </ul>
 */
public class CoffeeSelectionActivity
        extends AppCompatActivity
        implements View.OnClickListener, TransactionErrorCallback, TransactionSuccessCallback, TransactionBindCallback {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    /**
     * Coffee's unity price.
     */
    private static final double UNITY_PRICE = 2.65;

    /**
     * Minimum coffee amount.
     */
    private static final int MINIMUM_COFFEE_AMOUNT = 1;

    /**
     * Maximum number of retries in case of recoverable failures.
     */
    private static final int MAX_RETRIES = 5;

    /**
     * Error codes that should trigger a transaction retry.
     */
    private static final int[] RETRY_TRANSACTION_ERROR_CODES = {
            -1003,
            -2027,
            -2028
    };

    /**
     * ID used for generating and fetching tasks.
     */
    private static final long TASK_ID = 0x1234;

    /**
     * Retry counter Bundle key.
     */
    private static final String RETRY_COUNTER = "RETRY_COUNTER";

    // ---------------------------------------------------------------------------------------------
    // Views references
    // ---------------------------------------------------------------------------------------------

    private TextView mTxtCoffeeAmount = null;
    private TextView mTxtPrice = null;
    private Button mBtnPlus = null;
    private Button mBtnMinus = null;
    private Button mBtnPay = null;
    private Button mBtnCredit = null;
    private Button mBtnDebit = null;
    private ProgressDialog mThrobber = null;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private int mCoffeeAmount = CoffeeSelectionActivity.MINIMUM_COFFEE_AMOUNT;
    private int mPaymentMethod = PlugPag.CREDIT;
    private int mRetriesCounter = 0;
    private TransactionTask mTask = null;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_coffee_selection);
        this.setupViewReferences();
        this.setupEventListeners();
        this.updateCoffeeViews(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.mTask = TransactionTask.getInstance(CoffeeSelectionActivity.TASK_ID);

        if (this.mTask != null) {
            this.mTask.bind(this, this, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TransactionTask.store(this.mTask);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putInt(CoffeeSelectionActivity.RETRY_COUNTER, this.mRetriesCounter);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Views initialization
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups View references.
     */
    private void setupViewReferences() {
        this.mTxtCoffeeAmount = (TextView) this.findViewById(R.id.txtCoffeeAmount);
        this.mTxtPrice = (TextView) this.findViewById(R.id.txtTotalValue);
        this.mBtnPlus = (Button) this.findViewById(R.id.btnPlus);
        this.mBtnMinus = (Button) this.findViewById(R.id.btnMinus);
        this.mBtnPay = (Button) this.findViewById(R.id.btnPay);
        this.mBtnCredit = (Button) this.findViewById(R.id.btnCredit);
        this.mBtnDebit = (Button) this.findViewById(R.id.btnDebit);

        // Default payment method
        this.mBtnCredit.setSelected(true);
    }

    /**
     * Setups event listeners.
     */
    private void setupEventListeners() {
        this.mBtnPlus.setOnClickListener(this);
        this.mBtnMinus.setOnClickListener(this);
        this.mBtnPay.setOnClickListener(this);
        this.mBtnCredit.setOnClickListener(this);
        this.mBtnDebit.setOnClickListener(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Event listeners
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            // Credit Button click
            case R.id.btnCredit:
                this.selectPaymentMethod(PlugPag.CREDIT);
                break;

            // Debit Button click
            case R.id.btnDebit:
                this.selectPaymentMethod(PlugPag.DEBIT);
                break;

            // + Button clicked
            case R.id.btnPlus:
                this.updateCoffeeViews(1);
                break;

            // - Button clicked
            case R.id.btnMinus:
                this.updateCoffeeViews(-1);
                break;

            // Pay Button clicked
            case R.id.btnPay:
                this.mRetriesCounter = 0;
                this.proceedToPayment();
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Payment type selection
    // ---------------------------------------------------------------------------------------------

    /**
     * Selects the payment method.
     *
     * @param paymentMethod Payment method
     */
    private void selectPaymentMethod(int paymentMethod) {
        this.mBtnCredit.setSelected(false);
        this.mBtnDebit.setSelected(false);

        switch (paymentMethod) {
            case PlugPag.CREDIT:
                this.mBtnCredit.setSelected(true);
                break;

            case PlugPag.DEBIT:
                this.mBtnDebit.setSelected(true);
                break;
        }

        this.mPaymentMethod = paymentMethod;
    }

    // ---------------------------------------------------------------------------------------------
    // Coffee amount and price updates
    // ---------------------------------------------------------------------------------------------

    /**
     * Updates the coffee Views.
     *
     * @param amount Amount to be added to the current coffee counter.
     */
    private void updateCoffeeViews(int amount) {
        this.addCoffeeAmount(amount);
        this.updateCoffeeAmountDisplay();
        this.updateCoffeePriceDisplay();
        this.updateCoffeeAmountButtonsDisplay();
    }

    /**
     * Adds a coffee amount.
     *
     * @param amount Amount to be added.
     */
    private void addCoffeeAmount(int amount) {
        this.mCoffeeAmount += amount;

        if (this.mCoffeeAmount < CoffeeSelectionActivity.MINIMUM_COFFEE_AMOUNT) {
            this.mCoffeeAmount = CoffeeSelectionActivity.MINIMUM_COFFEE_AMOUNT;
        }
    }

    /**
     * Updates the coffee amount display.
     */
    private void updateCoffeeAmountDisplay() {
        this.mTxtCoffeeAmount.setText(this.getResources().getQuantityString(
                R.plurals.coffee, this.mCoffeeAmount, this.mCoffeeAmount));
    }

    /**
     * Updates the coffee value display.
     */
    private void updateCoffeePriceDisplay() {
        this.mTxtPrice.setText(this.getString(R.string.currency_with_value, this.getPrice()));
    }

    /**
     * Updates the coffee amount subtraction Button display
     */
    private void updateCoffeeAmountButtonsDisplay() {
        this.mBtnMinus.setEnabled(this.mCoffeeAmount > 1);
    }

    /**
     * Returns the total price.
     *
     * @return Total price calculated.
     */
    private double getPrice() {
        return this.mCoffeeAmount * CoffeeSelectionActivity.UNITY_PRICE;
    }

    // ---------------------------------------------------------------------------------------------
    // Buttons state handling
    // ---------------------------------------------------------------------------------------------

    /**
     * Changes all Buttons' enabled state.
     */
    public void setButtonsEnabled(boolean enabled) {
        this.mBtnCredit.setEnabled(enabled);
        this.mBtnDebit.setEnabled(enabled);
        this.mBtnMinus.setEnabled(enabled);
        this.mBtnPlus.setEnabled(enabled);
        this.mBtnPay.setEnabled(enabled);
    }

    // ---------------------------------------------------------------------------------------------
    // Start payment transaction
    // ---------------------------------------------------------------------------------------------

    /**
     * Proceeds to payment.
     */
    private void proceedToPayment() {
        this.showThrobber();
        this.setButtonsEnabled(false);
        this.startPaymentTransaction();
    }

    /**
     * Starts a payment transaction.
     */
    public void startPaymentTransaction() {
        TransactionTask task = null;
        String paymentValue = null;

        // Show throbber with updated retry counter
        this.mRetriesCounter++;
        this.showThrobber();

        // Calculate payment value
        paymentValue = String.format("%d", Math.round(this.getPrice() * 100.0));

        // Start payment task
        this.mTask = TransactionTask.getInstance(CoffeeSelectionActivity.TASK_ID);

        if (this.mTask == null) {
            this.mTask = TransactionTask.createInstance(this, this, CoffeeSelectionActivity.TASK_ID);
            this.mTask.execute(new Transaction(Type_Transaction.SIMPLE_PAYMENT, this.mPaymentMethod, paymentValue));
        } else {
            this.mTask.bind(this, this, this);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Task callback
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onSuccess(@Nullable Transaction result) {
        this.setButtonsEnabled(true);
        this.dismissThrobber();
        ResultFragment.newInstance(result).show(this.getSupportFragmentManager().beginTransaction(), "");
    }

    @Override
    public void onError(@Nullable Transaction result) {
        ResultFragment resultFragment = null;

        if (this.shouldRetryTransaction(result)) {
            // Retry transaction
            this.startPaymentTransaction();
        } else {
            this.dismissThrobber();
            this.setButtonsEnabled(true);

            // Show the error DialogFragment
            resultFragment = ResultFragment.newInstance(result);
            resultFragment.show(this.getSupportFragmentManager().beginTransaction(), "");
        }
    }

    @Override
    public void onBind() {
        this.showThrobber();
    }

    // ---------------------------------------------------------------------------------------------
    // Error handling
    // ---------------------------------------------------------------------------------------------

    /**
     * Checks if the transaction should be retried.
     *
     * @return If the transaction should be retried.
     */
    public boolean shouldRetryTransaction(@Nullable Transaction transactionResult) {
        boolean retry = false;
        int resultCode = 0;
        boolean isRetryableErrorCode = false;

        if (transactionResult != null ) {
            resultCode = transactionResult.getResult();

            // Check for retryable error codes
            for (int errorCode : CoffeeSelectionActivity.RETRY_TRANSACTION_ERROR_CODES) {
                if (errorCode == resultCode) {
                    isRetryableErrorCode = true;
                    break;
                }
            }

            // Check all conditions together
            retry = isRetryableErrorCode && CoffeeSelectionActivity.MAX_RETRIES > this.mRetriesCounter;
        }

        return retry;
    }

    // ---------------------------------------------------------------------------------------------
    // Throbber manipulation
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows the throbber if needed.
     */
    private void showThrobber() {
        if (this.mThrobber == null) {
            // Configure new throbber
            this.mThrobber = new ProgressDialog(this);
            this.mThrobber.setIndeterminate(true);
            this.mThrobber.setCancelable(false);
            this.mThrobber.setCanceledOnTouchOutside(false);
        }

        // Show throbber if needed
        if (!this.mThrobber.isShowing()) {
            this.mThrobber.show();
        }

        this.mThrobber.setMessage(
                this.getString(R.string.wait_with_count, this.mRetriesCounter));
    }

    /**
     * Dismisses the throbber.
     */
    private void dismissThrobber() {
        if (this.mThrobber != null && this.mThrobber.isShowing()) {
            this.mThrobber.dismiss();
        }
    }

}
