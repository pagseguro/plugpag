package br.uol.pagseguro.client.pagcafe.interfaces;

import br.uol.pagseguro.client.pagcafe.model.Transaction;

/**
 * Created by tqi_hsantos on 10/07/17.
 */

public interface TaskDelegate {

    void onTaskEndWithResult(Transaction result);

}
