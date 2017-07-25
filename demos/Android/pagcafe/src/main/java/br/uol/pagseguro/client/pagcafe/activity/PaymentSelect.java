package br.uol.pagseguro.client.pagcafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.task.TaskWithDelegate;
import br.uol.pagseguro.client.pagcafe.interfaces.TaskDelegate;
import br.uol.pagseguro.client.pagcafe.model.Type_Transaction;
import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.plugpag.PlugPag;

public class PaymentSelect extends AppCompatActivity implements TaskDelegate {

    private Button buttonCredit;
    private Button buttonDebit;

    private String samount;
    private  Context context;

    int paymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context  = (Context) this;

        setContentView(R.layout.activity_payment_select);

        Intent thisIntent = getIntent();

        Bundle extras = thisIntent.getExtras();
        Integer amountObj = (Integer) extras.get("amount");
        final int amount = amountObj.intValue();

        double price = amount;
        price = (double) Math.round(price * 1) / 100;

        samount = amountObj.toString();

        final TextView viewAmount = (TextView) findViewById(R.id.payPrice);

        viewAmount.setText("R$ " + Double.toString(price));

        final Handler handler =  new Handler();

        buttonCredit = (Button) findViewById(R.id.buttonCredit);
        buttonCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewAmount.setText("Aguarde");
                paymentMethod = PlugPag.CREDIT;

                createTransaction(); // Create a transaction
            }
        });

        buttonDebit = (Button) findViewById(R.id.buttonDebit);
        buttonDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewAmount.setText("Aguarde");
                paymentMethod = PlugPag.DEBIT;

                createTransaction(); // Create a transaction
            }
        });
    }

    private void createTransaction() {

        Transaction ts = new Transaction(Type_Transaction.SIMPLE_PAYMENT, paymentMethod, samount);

        TaskWithDelegate task = new TaskWithDelegate();
        task.setDelegate(this); //set the delegate of the task as this activity
        task.execute(ts);
    }

    @Override
    public void onTaskEndWithResult(Transaction result) {

        Intent intent = null;

        if (result.getResult() == PlugPag.RET_OK) {

            intent = new Intent(context, LastTransationActivity.class);
        }
        else{
            intent = new Intent(context, Result.class);
            intent.putExtra("transactionRetCode", result.getResult());
        }

        startActivity(intent);
        this.finish();
    }
}
