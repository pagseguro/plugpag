package br.uol.pagseguro.client.pagcafe.adapter;

import android.view.View;

/**
 * Created by tqi_hsantos on 10/07/17.
 */

public interface ClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
