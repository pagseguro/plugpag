package br.uol.pagseguro.client.pagcafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.adapter.ClickListener;
import br.uol.pagseguro.client.pagcafe.adapter.MenuAdapter;
import br.uol.pagseguro.client.pagcafe.adapter.RecyclerTouchListener;

public class MainActivity extends Activity {

    private List<String> menuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MenuAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        prepareData();

        mAdapter = new MenuAdapter(menuList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                goTo(position);
            }

            @Override
            public void onLongClick(View view, int position) {

                String menu = menuList.get(position);
                Toast.makeText(getApplicationContext(), "onLongClick " + menu + " is selected!", Toast.LENGTH_SHORT).show();
            }
        }));

    }

    protected void prepareData() {

        menuList = Arrays.asList(getResources().getStringArray(R.array.menus));
    }

    protected void goTo(int position) {

        Intent intent = null;

        switch (position) {

            case 0:
                intent = new Intent(this, CoffeeSelect.class);
                break;

            case 1:
                intent = new Intent(this, CancelTransactionActivity.class);
                break;

            case 2:
                intent = new Intent(this, LastTransationActivity.class);
                break;
        }

        startActivity(intent);
    }


}