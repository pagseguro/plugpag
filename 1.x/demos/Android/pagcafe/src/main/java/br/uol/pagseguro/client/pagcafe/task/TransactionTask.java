package br.uol.pagseguro.client.pagcafe.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.plugpag.PlugPag;


/**
 * <p>{@link AsyncTask} usada para solicitar transações ao terminal.</p>
 *
 * <p>{@link #APP_NAME}: Nome da aplicação.</p>
 * <p>{@link #APP_VERSION}: Versão da aplicação.</p>
 * <p>{@link #INSTALLMENTS}: Número de parcelas para fazer um pagamento.</p>
 * <p>{@link #USER_REFERENCE}: Referência do usuário que está fazendo a transação.</p>
 *
 * <p>A utilização do método {@link #createInstance} deve ser utilizado para criar novas instâncias
 * dessa <i>task</i>.</p>
 *
 * <p>Novas instâncias dessa <i>task</i> recebem 3 parâmetros:</p>
 * <ul>
 *     <li><{@link TransactionSuccessCallback}: Callback chamado quando a task for executada com
 *     sucesso.</li>
 *     <li><{@link TransactionErrorCallback}: Callback chamado quando a task for executada com
 *     erro.</li>
 *     <li>ID da <i>task</i>.</li>
 * </ul>
 *
 * <p>Os métodos {@link #store} e {@link #release} servem, respectivamente, para armazenar e liberar
 * referências da <i>task</i>. Esses métodos foram criados para simplificar o controle de
 * reaproveitamento das <i>task</i>s quando houver algum evento de ciclo de vida da
 * <i>Activity</i> ou do <i>Fragment</i> que utilizá-la.</p>
 *
 * <p>Os métodos {@link #bind} e {@link #unbind} servem para controlar as referências dos callbacks
 * da <i>task</i> para reduzir a chance de memory leak, liberando referências que não devem
 * permanecer por longos períodos na memória.</p>
 *
 * <p>Ao final do método {@link #doInBackground}, se a <i>task</i> tiver perdido a referência dos
 * callbacks, é realizado um <i>busy wait</i> para aguardar novo <i>bind</i> de callbacks para que
 * o usuário tenha algum tipo de feedback. Essa técnica simples também serve para controlar
 * momentos nos quais há algum tipo de evento de ciclo de vida da <i>Activity</i> ou do
 * <i>Fragment</i> que manipula essa <i>task</i>.</p>
 */
public class TransactionTask extends AsyncTask<Transaction, Void, Transaction> {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    private static final String APP_NAME = "PagCafe";
    private static final String APP_VERSION = "R001";

    private static final int INSTALLMENTS = 1;
    private static final String USER_REFERENCE = "pagcafe";

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    /**
     * Transactions repository.
     */
    private static final Map<Long, TransactionTask> sTasks = new HashMap<>();

    // ---------------------------------------------------------------------------------------------
    // Task creation/fetch
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new task instance.
     *
     * @param onSuccess Callback called when the task has succeeded.
     * @param onError   Callback called when the task has returned an error.
     * @param id        Task ID.
     * @return New task instance.
     */
    public static final TransactionTask createInstance(@NonNull TransactionSuccessCallback onSuccess,
                                                       @NonNull TransactionErrorCallback onError,
                                                       long id) {
        TransactionTask task = null;

        task = new TransactionTask(onSuccess, onError);
        task.mId = id;
        TransactionTask.sTasks.put(id, task);

        return task;
    }

    /**
     * Returns a task that was stored in the repository.
     *
     * @param id Task ID.
     * @return Task found in the repository.
     */
    public static final TransactionTask getInstance(long id) {
        return TransactionTask.sTasks.get(id);
    }

    /**
     * Stores a task into the repository.
     *
     * @param task Task to be stored.
     */
    public static final void store(@NonNull TransactionTask task) {
        if (task != null) {
            task.unbind();
            TransactionTask.sTasks.put(task.mId, task);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Task release
    // ---------------------------------------------------------------------------------------------

    /**
     * Releases a task reference.
     *
     * @param id Task ID.
     */
    private static final void release(long id) {
        TransactionTask task = null;

        if (TransactionTask.sTasks.containsKey(id)) {
            task = TransactionTask.sTasks.get(id);
            task.mSuccessCallback = null;
            task.mErrorCallback = null;
            task.mId = -1;
            TransactionTask.sTasks.remove(id);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private long mId = -1;
    private PlugPag mPlugPag = null;
    private TransactionSuccessCallback mSuccessCallback = null;
    private TransactionErrorCallback mErrorCallback = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new TransactionTask.<br />
     * Use the method {@link TransactionTask#createInstance} to create new instances.
     *
     * @param onSuccess Callback called when task has succeeded.
     * @param onError   Callback called when the task has returned an error.
     */
    private TransactionTask(@NonNull TransactionSuccessCallback onSuccess,
                            @NonNull TransactionErrorCallback onError) {
        this.mSuccessCallback = onSuccess;
        this.mErrorCallback = onError;
    }

    // ---------------------------------------------------------------------------------------------
    // PlugPag setup
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups a PlugPag instance.
     */
    protected final void setupPlugPag() {
        this.mPlugPag = new PlugPag();
        this.mPlugPag.SetVersionName(TransactionTask.APP_NAME, TransactionTask.APP_VERSION);
        this.mPlugPag.InitBTConnection();
    }

    // ---------------------------------------------------------------------------------------------
    // Task lifecycle
    // ---------------------------------------------------------------------------------------------

    @Override
    protected Transaction doInBackground(Transaction... params) {
        Transaction transaction = null;

        if (params != null && params.length > 0 && params[0] != null) {
            try {
                this.setupPlugPag();
                transaction = new Transaction(params[0]);

                switch (transaction.getType()) {
                    // Cancel a transaction
                    case CANCEL_TRANSACTION:
                        transaction.setResult(mPlugPag.CancelTransaction());
                        break;

                    // Fetch last successful transaction's result
                    case GET_LAST_TRANSACTION:
                        transaction.setResult(mPlugPag.GetLastApprovedTransactionStatus());
                        break;

                    // Starts a simple payment transaction
                    case SIMPLE_PAYMENT:
                        transaction.setResult(mPlugPag.SimplePaymentTransaction(
                                transaction.getPaymentMethod(),
                                PlugPag.A_VISTA,
                                TransactionTask.INSTALLMENTS,
                                transaction.getAmount(),
                                TransactionTask.USER_REFERENCE));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Busy wait for callbacks to be ready
        while (this.mSuccessCallback == null || this.mErrorCallback == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return transaction;
    }

    @Override
    protected void onPostExecute(Transaction result) {
        TransactionSuccessCallback successCallback = null;
        TransactionErrorCallback errorCallback = null;

        successCallback = this.mSuccessCallback;
        errorCallback = this.mErrorCallback;

        // Release references to prevent memory leak
        this.mSuccessCallback = null;
        this.mErrorCallback = null;

        // Setup result
        if (result != null) {
            result.setMessage(this.mPlugPag.getMessage());
            result.setTransactionCode(this.mPlugPag.getTransactionCode());
            result.setDate(this.mPlugPag.getDate());
            result.setTime(this.mPlugPag.getTime());
            result.setHostNsu(this.mPlugPag.getHostNsu());
            result.setCardBrand(this.mPlugPag.getCardBrand());
            result.setBin(this.mPlugPag.getBin());
            result.setHolder(this.mPlugPag.getHolder());
            result.setUserReference(this.mPlugPag.getUserReference());
            result.setTerminalSerialNumber(this.mPlugPag.getTerminalSerialNumber());
        }

        // Invoke callbacks
        if (result == null || result.getResult() != PlugPag.RET_OK) {
            if (errorCallback != null) {
                errorCallback.onError(result);
            }
        } else {
            if (successCallback != null) {
                successCallback.onSuccess(result);
            }
        }

        TransactionTask.release(this.mId);
    }

    // ---------------------------------------------------------------------------------------------
    // Parent binding
    // ---------------------------------------------------------------------------------------------

    /**
     * Unbinds from the parent.
     */
    public void unbind() {
        this.mSuccessCallback = null;
        this.mErrorCallback = null;
    }

    /**
     * Binds the task to a new parent.
     *
     * @param successCallback Callback called when the task has succeeded.
     * @param errorCallback   Callback called when the task has returned an error.
     * @param bindCallback    Callback called when the task has been bound.
     */
    public void bind(@NonNull TransactionSuccessCallback successCallback,
                     @NonNull TransactionErrorCallback errorCallback,
                     @NonNull TransactionBindCallback bindCallback) {
        this.mSuccessCallback = successCallback;
        this.mErrorCallback = errorCallback;

        if (bindCallback != null) {
            bindCallback.onBind();
        }
    }

}
