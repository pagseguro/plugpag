package br.com.uol.pagseguro.plugpag.pagcafe.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Bluetooth {

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    private static BluetoothDevice sSelectedBluetoothDevice = null;

    private static List<BluetoothObservable> sObservables = null;

    // ---------------------------------------------------------------------------------------------
    // Getter/setters
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the selected bluetooth device that must be used for transaction.
     *
     * @return Bluetooth device that must be used for transactions.
     */
    public static BluetoothDevice getSelectedBluetoothDevice() {
        return Bluetooth.sSelectedBluetoothDevice;
    }

    /**
     * Sets the reference to the bluetooth device that must be used for transactions.
     *
     * @param device Reference of the bluetooth device that must be used for transactions.
     */
    public static void setSelectedBluetoothDevice(@Nullable BluetoothDevice device) {
        Bluetooth.sSelectedBluetoothDevice = device;

        if (Bluetooth.sObservables != null && Bluetooth.sObservables.size() > 0) {
            for (BluetoothObservable obs : Bluetooth.sObservables) {
                obs.onSetBluetoothDevice(device);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Observable manipulation
    // ---------------------------------------------------------------------------------------------

    /**
     * Adds an observable to listen for changes on the bluetooth device selection.
     *
     * @param observable Observable to be added.
     */
    public static void register(BluetoothObservable observable) {
        if (Bluetooth.sObservables == null) {
            Bluetooth.sObservables = new ArrayList<>();
        }

        Bluetooth.sObservables.add(observable);
    }

    /**
     * Removes an observable.
     *
     * @param observable Observable to be removed.
     */
    public static void unregister(BluetoothObservable observable) {
        if (Bluetooth.sObservables == null) {
            Bluetooth.sObservables = new ArrayList<>();
        }

        if (observable != null) {
            Bluetooth.sObservables.remove(observable);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Observable interface
    // ---------------------------------------------------------------------------------------------

    /**
     * Bluetooth device selection observable.
     */
    public interface BluetoothObservable {

        /**
         * Called when a bluetooth device is set.
         *
         * @param device Bluetooth device set.
         */
        void onSetBluetoothDevice(@Nullable BluetoothDevice device);
    }

}
