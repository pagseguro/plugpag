package br.com.uol.pagseguro.plugpag.pagcafe.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;


public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private OnNetworkChanged mObserver = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new BroadcastReceiver to handle network changes.
     *
     * @param observer Observer to be notified when a network change occurs.
     */
    public NetworkChangeBroadcastReceiver(@NonNull OnNetworkChanged observer) {
        this.mObserver = observer;
    }

    // ---------------------------------------------------------------------------------------------
    // Callback
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        if (this.mObserver != null) {
            // Gather connection information
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            ni = cm.getActiveNetworkInfo();

            // Call observer
            this.mObserver.onNetworkChanged(ni);
        }
    }

}
