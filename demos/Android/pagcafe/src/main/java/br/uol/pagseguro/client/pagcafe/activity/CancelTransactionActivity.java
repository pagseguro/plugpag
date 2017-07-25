package br.uol.pagseguro.client.pagcafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.task.TaskWithDelegate;
import br.uol.pagseguro.client.pagcafe.interfaces.TaskDelegate;
import br.uol.pagseguro.client.pagcafe.model.Type_Transaction;
import br.uol.pagseguro.client.pagcafe.model.Transaction;

public class CancelTransactionActivity extends Activity implements TaskDelegate {

    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_transaction);

        pb = (ProgressBar) findViewById(R.id.pb);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //create a transaction
                createTransaction();
            }
        }, 1000);
    }

    private void createTransaction() {

        Transaction ts = new Transaction(Type_Transaction.CANCEL_TRANSACTION); // Create a cancel transaction

        TaskWithDelegate task = new TaskWithDelegate();
        task.setDelegate(this); //set the delegate of the task as this activity
        task.execute(ts);
    }

    @Override
    public void onTaskEndWithResult(Transaction result) {

        pb.setVisibility(View.INVISIBLE);

        Intent intent = new Intent(this, LastTransationActivity.class);

        startActivity(intent);
        this.finish();
    }
}
