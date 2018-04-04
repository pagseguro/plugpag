package br.com.uol.pagseguro.plugpag.pagcafe.bluetooth;

public interface OnBluetoothStateChanged {

    /**
     * Called when a bluetooth state is changed.
     *
     * @param state New bluetooth state.
     */
    void onBluetoothStateChanged(int state);

}
