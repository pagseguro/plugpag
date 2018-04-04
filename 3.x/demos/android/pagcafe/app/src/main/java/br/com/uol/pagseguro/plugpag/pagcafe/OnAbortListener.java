package br.com.uol.pagseguro.plugpag.pagcafe;

import android.support.annotation.NonNull;

import java.io.Serializable;

import br.com.uol.pagseguro.plugpag.PlugPag;


public interface OnAbortListener extends Serializable {

    void onAbort(@NonNull PlugPag plugpag);

}
