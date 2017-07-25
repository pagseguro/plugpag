package br.uol.pagseguro.client.pagcafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.task.TaskWithDelegate;
import br.uol.pagseguro.client.pagcafe.interfaces.TaskDelegate;
import br.uol.pagseguro.client.pagcafe.model.Type_Transaction;
import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.plugpag.PlugPag;

public class LastTransationActivity extends Activity implements TaskDelegate {

    private TextView tvMessage;
    private TextView tvTransactionCode;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvHostNsu;
    private TextView tvCardBrand;
    private TextView tvBin;
    private TextView tvHolder;
    private TextView tvUserReference;
    private TextView tvSerialNumber;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_transation);

        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvTransactionCode = (TextView) findViewById(R.id.tvTransactionCode);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvHostNsu = (TextView) findViewById(R.id.tvHostNsu);
        tvCardBrand = (TextView) findViewById(R.id.tvCardBrand);
        tvBin = (TextView) findViewById(R.id.tvBin);
        tvHolder = (TextView) findViewById(R.id.tvHolder);
        tvUserReference = (TextView) findViewById(R.id.tvUserReference);
        tvSerialNumber = (TextView) findViewById(R.id.tvSerialNumber);
        pb = (ProgressBar) findViewById(R.id.pb);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create a transaction
                createTransaction();
            }
        }, 1000);
    }

    private void createTransaction() {

        Transaction ts = new Transaction(Type_Transaction.GET_LAST_TRANSACTION);

        TaskWithDelegate task = new TaskWithDelegate();
        task.setDelegate(this); //set the delegate of the task as this activity
        task.execute(ts);
    }

    @Override
    public void onTaskEndWithResult(Transaction result) {

        if(result.getResult() == PlugPag.RET_OK) {

            tvMessage.setText(result.getMessage());
            tvTransactionCode.setText(result.getTransactionCode());
            tvDate.setText(result.getDate());
            tvTime.setText(result.getTime());
            tvHostNsu.setText(result.getHostNsu());
            tvCardBrand.setText(result.getCardBrand());
            tvBin.setText(result.getBin());
            tvHolder.setText(result.getHolder());
            tvUserReference.setText(result.getUserReference());
            tvSerialNumber.setText(result.getTerminalSerialNumber());

        } else {
            tvMessage.setText("ERRO: "+ result);
        }

        pb.setVisibility(View.INVISIBLE);
    }
}
