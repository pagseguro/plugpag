package br.uol.pagseguro.client.pagcafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.plugpag.PlugPag;

public class Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView viewReult = (TextView)findViewById(R.id.resultText);

        int result = getIntent().getIntExtra("transactionRetCode", 0);

        if(result == PlugPag.RET_OK)
        {
            viewReult.setText("Transação   OK");
        }
        else
        {
            viewReult.setText("ERRO: "+ result);
        }

    }
}
