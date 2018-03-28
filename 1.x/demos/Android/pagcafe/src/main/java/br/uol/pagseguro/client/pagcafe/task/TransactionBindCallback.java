package br.uol.pagseguro.client.pagcafe.task;

import br.uol.pagseguro.client.pagcafe.model.Transaction;


public interface TransactionBindCallback {

    /**
     * Called when the transaction task has been bound.
     */
    void onBind();

}
