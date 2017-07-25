package br.uol.pagseguro.client.pagcafe.task;

import android.os.AsyncTask;

import br.uol.pagseguro.client.pagcafe.interfaces.TaskDelegate;
import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.plugpag.PlugPag;

/**
 * Created by tqi_hsantos on 10/07/17.
 */

public final class TaskWithDelegate extends AsyncTask<Transaction, Void, Transaction> {

    //declare a delegate with type of protocol declared in this task
    private TaskDelegate delegate;
    private PlugPag plugPag;
    private Transaction ts;

    @Override
    protected Transaction doInBackground(Transaction... params) {

        plugPag = new PlugPag();
        plugPag.SetVersionName("PagCafe", "R001");
        plugPag.InitBTConnection();

        ts = params[0];

        switch (ts.getType()) {

            case CANCEL_TRANSACTION:
                ts.setResult(plugPag.CancelTransaction());
                break;

            case GET_LAST_TRANSACTION:
                ts.setResult(plugPag.GetLastApprovedTransactionStatus());
                break;

            case SIMPLE_PAYMENT:
                ts.setResult(plugPag.SimplePaymentTransaction(
                        ts.getPaymentMethod(),
                        PlugPag.A_VISTA,
                        1, ts.getSamount(), "pagcafe"));
                break;
        }
        return ts;
    }

    @Override
    protected void onPostExecute(Transaction result) {

        result.setMessage(plugPag.getMessage());
        result.setTransactionCode(plugPag.getTransactionCode());
        result.setDate(plugPag.getDate());
        result.setTime(plugPag.getTime());
        result.setHostNsu(plugPag.getHostNsu());
        result.setCardBrand(plugPag.getCardBrand());
        result.setBin(plugPag.getBin());
        result.setHolder(plugPag.getHolder());
        result.setUserReference(plugPag.getUserReference());
        result.setTerminalSerialNumber(plugPag.getTerminalSerialNumber());

        if (delegate != null) {
            //return success or fail to activity
            delegate.onTaskEndWithResult(result);
        }
    }

    public void setDelegate(TaskDelegate delegate) {
        this.delegate = delegate;
    }
}
