package br.com.uol.pagseguro.plugpag.pagcafe.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class BluetoothStateChangeBroadcastReceiver extends BroadcastReceiver {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private OnBluetoothStateChanged mObserver = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new BroadcastReceiver to listen for bluetooth state changes.
     *
     * @param observer Observer to be called when the bluetooth state changes.
     */
    public BluetoothStateChangeBroadcastReceiver(@NonNull OnBluetoothStateChanged observer) {
        this.mObserver = observer;
    }

    // ---------------------------------------------------------------------------------------------
    // Callback
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.mObserver != null &&
                intent != null &&
                intent.getAction() != null &&
                BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            this.mObserver.onBluetoothStateChanged(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
        }
    }

}
