package br.uol.pagseguro.client.pagcafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.uol.pagseguro.client.pagcafe.R;


public class CoffeeSelect extends AppCompatActivity {

    private Button buttonPlus;
    private Button buttonMinus;
    private TextView viewCount;
    private TextView viewPrice;
    private int count = 1;
    private double price = 2.65;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = (Context) this;

        setContentView(R.layout.activity_coffee_select);

        viewCount = (TextView) findViewById(R.id.nCoffee);
        viewPrice = (TextView) findViewById(R.id.price);

        buttonPlus = (Button) findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count==1) {
                    viewCount.setText(Integer.toString(count) + " Café");
                }
                else {
                    viewCount.setText(Integer.toString(count) + " Cafés");
                }

                price = count * (float)2.65;
                price = (double)Math.round(price *100) / 100;

                viewPrice.setText("R$ "+Double.toString(price));
            }
        });

        buttonMinus = (Button) findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                if(count<=0) count=1;

                if(count==1)
                    viewCount.setText(Integer.toString(count)+ " Café");
                else
                    viewCount.setText(Integer.toString(count)+ " Cafés");

                price = count * (float)2.65;
                price = (double)Math.round(price *100) / 100;

                viewPrice.setText("R$ "+Double.toString(price));

            }
        });

        Button buttonPay = (Button) findViewById(R.id.pay);


        buttonPay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentSelect.class);
                Bundle b = new Bundle();

                b.putInt("amount", (int)(price*100));
                //intent.putExtra("EXTRA_AMOUNT", (int)price*10);

                intent.putExtras(b);

                startActivity(intent);

                count = 1;
                viewCount.setText(Integer.toString(count)+ " Café");
                price = count * (float)2.65;
                price = (double)Math.round(price *100) / 100;
                viewPrice.setText("R$ "+Double.toString(price));
            }
        });
    }


}
