package br.uol.pagseguro.client.pagcafe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.uol.pagseguro.client.pagcafe.LastSuccessfulTransaction;
import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.pagcafe.model.Type_Transaction;
import br.uol.pagseguro.client.pagcafe.task.TransactionErrorCallback;
import br.uol.pagseguro.client.pagcafe.task.TransactionSuccessCallback;
import br.uol.pagseguro.client.pagcafe.task.TransactionTask;
import br.uol.pagseguro.client.plugpag.PlugPag;


/**
 * <p>{@link DialogFragment} para exibir resultados de transações.</p>
 * <p>
 * <p>As informações a serem exibidas podem ser passadas para esse {@link DialogFragment} por meio
 * do {@link android.content.Intent} passado para o método {@link #newInstance}, na chave
 * {@link #TRANSACTION}.</p>
 * <p>
 * <p>Há 2 modos de exibição possíveis: Erro e Informações de transação.</p>
 * <p>
 * <p>{@link #onCreateView}: Carrega o <i>resource</i> de layout e configura o
 * {@link DialogFragment}.</p>
 * <p>
 * <p>{@link #onViewCreated}: Cria referências para as Views, define seus event listeners e decide
 * se o resultado de transação recebido deve ser exibido ou se deve ser feita uma consulta da
 * última venda realizada com sucesso.</p>
 * <p>
 * <p>Os métodos {@link #onSuccess} e {@link #onError} são chamados quando as operações executadas pela
 * <i>TransactionTask</i> finalizarem, respectivamente, com sucesso ou com erro.</p>
 * <p>
 * <ul>
 * <li>{@link #onSuccess}: Verifica se houve uma falha da última venda ou não, exibindo
 * um conteúdo diferente dependendo do resultado da verificação.</li>
 * <p>
 * <li>{@link #onError}: Exibe o erro recebido pelo método {@link #newInstance} ou o erro obtido
 * por uma das operações desse {@link DialogFragment}.</li>
 * </ul>
 */
public class ResultFragment
        extends DialogFragment
        implements TransactionErrorCallback, TransactionSuccessCallback, View.OnClickListener {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    /**
     * Bundle key where a Transaction is stored.
     */
    private static final String TRANSACTION = "TRANSACTION";

    /**
     * Default error detail messages to be displayed in case the Transaction's message is empty.
     */
    private static final SparseArray<String> ERROR_DETAILS = new SparseArray<String>() {
        { put(-1001, "Mensagem gerada maior do que o buffer dimensionado"); }
        { put(-1002, "Parâmetro de aplicação inválido"); }
        { put(-1003, "Terminal não está pronto para transacionar"); }
        { put(-1004, "Transação negada pelo host"); }
        { put(-1005, "Buffer de resposta da transação inválido ao obter as informações de resultado da transação"); }
        { put(-1006, "Parâmetro de valor da transação não pode ser nulo"); }
        { put(-1007, "Parâmetro de valor total da transação não pode ser nulo"); }
        { put(-1008, "Parâmetro de código de venda não pode ser nulo"); }
        { put(-1009, "Parâmetro de resultado da transação não pode ser nulo"); }
        { put(-1010, "Driver de conexão não enontrado"); }
        { put(-1011, "Erro ao utilizar driver de conexão"); }
        { put(-1012, "Formato do valor da venda inválido"); }
        { put(-1013, "Comprimento do REF superior a 10 dígitos"); }
        { put(-1014, "Buffer de recepção corrompido"); }
        { put(-1015, "Nome da aplicação maior do que 25 caracteres"); }
        { put(-1016, "Versão da aplicação maior do que 10 caracteres"); }
        { put(-1017, "Necessário definir nome da aplicação"); }
        { put(-1018, "Não existem dados da última transação"); }
        { put(-1019, "Erro de comunicação com terminal (resposta inesperada)"); }
    };

    /**
     * Errors that should bypass the query for the last successful transaction.
     */
    private static final int[] ERRORS_BYPASS = {
            -1004,
            -1018,
            -1019,
            -1020
    };

    /**
     * Task ID.
     */
    private static final int TASK_ID = 0x9999;

    // ---------------------------------------------------------------------------------------------
    // View references
    // ---------------------------------------------------------------------------------------------

    private ViewGroup mContainerResult = null;
    private ViewGroup mContainerError = null;
    private ViewGroup mContainerThrobber = null;

    private TextView mTxtThrobberMessage = null;

    private Button mBtnOk = null;

    private TextView mTxtErrorCode = null;
    private TextView mTxtErrorDetails = null;

    private TextView mTxtMessage = null;
    private TextView mTxtTransactionCode = null;
    private TextView mTxtDate = null;
    private TextView mTxtTime = null;
    private TextView mTxtHostNsu = null;
    private TextView mTxtCardBrand = null;
    private TextView mTxtBin = null;
    private TextView mTxtHolder = null;
    private TextView mTxtUserReference = null;
    private TextView mTxtSerialNumber = null;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    /**
     * Flag to indicate if a transaction cancellation was requested.
     */
    private boolean mCancellationRequested = false;

    /**
     * Flag to indicate if a Transaction should be compared with another.
     * This flag is used in case the given transaction error requires a query to the last successful
     * transaction.
     */
    private boolean mCompareLastTransaction = false;

    // ---------------------------------------------------------------------------------------------
    // DialogFragment creation
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of the ResultFragment.
     * If a Transaction result is given, it's value will be displayed.
     * If no Transaction result is given, there will be an attempt to load the last successful transaction.
     *
     * @param result Transaction result to be displayed.
     * @return Fragment to be displayed.
     */
    public static final ResultFragment newInstance(Transaction result) {
        ResultFragment fragment = null;
        Bundle args = null;

        args = new Bundle();
        args.putSerializable(ResultFragment.TRANSACTION, result);

        fragment = new ResultFragment();
        fragment.setArguments(args);

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
        view = inflater.inflate(R.layout.fragment_result, container, false);

        // Disable cancellation/dismissal of the DialogFragment to prevent unintentional dismissal
        // by pressing the Back button or touching outside the DialogFragment
        this.setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Transaction transactionResult = null;

        super.onViewCreated(view, savedInstanceState);
        this.setupViewReferences(view);
        transactionResult = this.getTransaction();

        if (transactionResult == null) {
            // Load last successful transaction
            this.mCompareLastTransaction = false;
            this.loadLastSuccessfulTransaction();
        } else {
            if (transactionResult.getResult() == PlugPag.RET_OK) {
                if (!this.mCancellationRequested &&
                        transactionResult.getType().equals(Type_Transaction.CANCEL_TRANSACTION)) {
                    // Start cancellation task
                    this.mCancellationRequested = true;
                    this.startTransactionCancellationTransaction();
                } else {
                    // Show transaction result
                    this.showTransactionResult(transactionResult);
                }
            } else {
                if (this.isBypassableError(transactionResult.getResult())) {
                    // Bypassable error: may be directly displayed
                    this.showError(transactionResult);
                } else {
                    // Query last successful transaction
                    this.mCompareLastTransaction = true;
                    this.loadLastSuccessfulTransaction();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // ---------------------------------------------------------------------------------------------
    // Views initialization
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups View references.
     *
     * @param rootView Root View.
     */
    private void setupViewReferences(View rootView) {
        this.mContainerResult = (ViewGroup) rootView.findViewById(R.id.container_result);
        this.mContainerError = (ViewGroup) rootView.findViewById(R.id.container_error);
        this.mContainerThrobber = (ViewGroup) rootView.findViewById(R.id.container_throbber);

        this.mTxtThrobberMessage = (TextView) rootView.findViewById(R.id.txtThrobberMessage);

        this.mBtnOk = (Button) rootView.findViewById(R.id.btnOk);
        this.mBtnOk.setOnClickListener(this);

        this.mTxtErrorCode = (TextView) rootView.findViewById(R.id.txtErrorCode);
        this.mTxtErrorDetails = (TextView) rootView.findViewById(R.id.txtErrorDetails);

        this.mTxtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
        this.mTxtTransactionCode = (TextView) rootView.findViewById(R.id.txtTransactionCode);
        this.mTxtDate = (TextView) rootView.findViewById(R.id.txtDate);
        this.mTxtTime = (TextView) rootView.findViewById(R.id.txtTime);
        this.mTxtHostNsu = (TextView) rootView.findViewById(R.id.txtHostNsu);
        this.mTxtCardBrand = (TextView) rootView.findViewById(R.id.txtCardBrand);
        this.mTxtBin = (TextView) rootView.findViewById(R.id.txtBin);
        this.mTxtHolder = (TextView) rootView.findViewById(R.id.txtHolder);
        this.mTxtUserReference = (TextView) rootView.findViewById(R.id.txtUserReference);
        this.mTxtSerialNumber = (TextView) rootView.findViewById(R.id.txtSerialNumber);
    }

    // ---------------------------------------------------------------------------------------------
    // OK Button click
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        this.dismiss();
    }

    // ---------------------------------------------------------------------------------------------
    // Arguments handling
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the associated Transaction.
     *
     * @return Associated Transaction.
     */
    private Transaction getTransaction() {
        Transaction transaction = null;
        Bundle args = null;

        args = this.getArguments();

        if (args != null && args.containsKey(ResultFragment.TRANSACTION)) {
            transaction = (Transaction) args.getSerializable(ResultFragment.TRANSACTION);
        }

        return transaction;
    }

    // ---------------------------------------------------------------------------------------------
    // Throbber manipulation
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows the throbber if needed.
     */
    private void showThrobber() {
        this.mTxtThrobberMessage.setText(R.string.wait);
        this.mContainerResult.setVisibility(View.GONE);
        this.mContainerError.setVisibility(View.GONE);
        this.mContainerThrobber.setVisibility(View.VISIBLE);
        this.mBtnOk.setVisibility(View.GONE);
    }

    // ---------------------------------------------------------------------------------------------
    // Task to load last successful transaction
    // ---------------------------------------------------------------------------------------------

    /**
     * Starts a task to load the last successful transaction.
     */
    private void loadLastSuccessfulTransaction() {
        Transaction ts = null;
        TransactionTask task = null;

        this.showThrobber();

        // Start the task to query the last successful transaction
        ts = new Transaction(Type_Transaction.GET_LAST_TRANSACTION);

        task = TransactionTask.createInstance(this, this, ResultFragment.TASK_ID);
        task.execute(ts);
    }

    /**
     * Starts a task to cancel a transaction.
     */
    private void startTransactionCancellationTransaction() {
        Transaction ts = null;
        TransactionTask task = null;

        this.showThrobber();

        // Start the task to request a transaction cancellation
        ts = new Transaction(Type_Transaction.CANCEL_TRANSACTION);

        task = TransactionTask.createInstance(this, this, ResultFragment.TASK_ID);
        task.execute(ts);
    }

    // ---------------------------------------------------------------------------------------------
    // Error display
    // ---------------------------------------------------------------------------------------------

    /**
     * Checks if an error code may be ignored and just displayed or if it needs to be compared with
     * the last successful transaction.
     *
     * @param errorCode Error code to be checked.
     * @return If the error may be ignored.
     */
    private boolean isBypassableError(int errorCode) {
        boolean bypassable = false;

        for (int err : ResultFragment.ERRORS_BYPASS) {
            if (errorCode == err) {
                bypassable = true;
                break;
            }
        }

        return bypassable;
    }

    /**
     * Shows the Transaction's error.
     *
     * @param result Transaction data.
     */
    private void showError(@NonNull Transaction result) {
        int code = 0;

        if (result == null) {
            throw new RuntimeException("Transaction result reference cannot be null");
        }

        // Fill Views
        code = result.getResult();
        this.mTxtErrorCode.setText(this.getString(R.string.error_code, code));

        if (!TextUtils.isEmpty(result.getMessage())) {
            // Show the result's error message
            this.mTxtErrorDetails.setText(result.getMessage());
        } else if (ResultFragment.ERROR_DETAILS.get(code) != null) {
            // Show a default error message
            this.mTxtErrorDetails.setText(ResultFragment.ERROR_DETAILS.get(code));
        } else {
            // Show no error message
            this.mTxtErrorDetails.setText("");
        }

        // Show error and hide other Views
        this.mContainerResult.setVisibility(View.GONE);
        this.mContainerError.setVisibility(View.VISIBLE);
        this.mContainerThrobber.setVisibility(View.GONE);
        this.mBtnOk.setVisibility(View.VISIBLE);
    }

    /**
     * Shows an error message.
     *
     * @param errorMessage
     */
    private void showError(@NonNull String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            throw new RuntimeException("Transaction result reference cannot be null");
        }

        // Fill Views
        this.mTxtErrorCode.setText("");
        this.mTxtErrorDetails.setText(errorMessage);

        // Show error and hide other Views
        this.mContainerResult.setVisibility(View.GONE);
        this.mContainerError.setVisibility(View.VISIBLE);
        this.mContainerThrobber.setVisibility(View.GONE);
        this.mBtnOk.setVisibility(View.VISIBLE);
    }

    // ---------------------------------------------------------------------------------------------
    // Task callback
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onError(Transaction result) {
        if (this.getTransaction() != null && !this.mCancellationRequested) {
            // If there was a previous error, show the original error
            this.showError(this.getTransaction());
        } else {
            // Show the new error
            this.showError(result);
        }
    }

    @Override
    public void onSuccess(Transaction result) {
        if (this.mCompareLastTransaction) {
            if (LastSuccessfulTransaction.transaction != null &&
                    LastSuccessfulTransaction.equals(result)) {
                this.showError(this.getString(R.string.last_transaction_failed));
            } else {
                this.showTransactionResult(result);
            }
        } else {
            // Shows the result of a query to the last successful transaction
            this.showTransactionResult(result);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Transaction result display
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows the transaction result.
     *
     * @param result Transaction result to be displayed.
     */
    private void showTransactionResult(@NonNull Transaction result) {
        if (result == null) {
            throw new RuntimeException("Transaction result reference cannot be null");
        }

        if (result != null) {
            LastSuccessfulTransaction.transaction = result;
        }

        // Fill Views
        this.mTxtMessage.setText(result.getMessage());
        this.mTxtTransactionCode.setText(result.getTransactionCode());
        this.mTxtDate.setText(result.getDate());
        this.mTxtTime.setText(result.getTime());
        this.mTxtHostNsu.setText(result.getHostNsu());
        this.mTxtCardBrand.setText(result.getCardBrand());
        this.mTxtBin.setText(result.getBin());
        this.mTxtHolder.setText(result.getHolder());
        this.mTxtUserReference.setText(result.getUserReference());
        this.mTxtSerialNumber.setText(result.getTerminalSerialNumber());

        // Show error and hide other Views
        this.mContainerResult.setVisibility(View.VISIBLE);
        this.mContainerError.setVisibility(View.GONE);
        this.mContainerThrobber.setVisibility(View.GONE);
        this.mBtnOk.setVisibility(View.VISIBLE);
    }

}
