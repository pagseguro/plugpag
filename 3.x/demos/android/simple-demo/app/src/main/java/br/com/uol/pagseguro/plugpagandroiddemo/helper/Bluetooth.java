package br.com.uol.pagseguro.plugpagandroiddemo.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.Set;

public class Bluetooth {

    // -----------------------------------------------------------------------------------------------------------------
    // Bluetooth name fetcher
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the first bluetooth terminal found.
     *
     * @return First bluetooth terminal found.
     */
    public static final String getTerminal() {
        return Bluetooth.findBluetoothDevice(new String[] { "PRO-", "W-", "W+-", "PLUS-", "MCHIP-" });
    }

    /**
     * Returns the first bluetooth pinpad found.
     *
     * @return First bluetooth pinpad found.
     */
    public static final String getPinpad() {
        return Bluetooth.findBluetoothDevice(new String[] { "PAX-", "MOBI-", "MOBIPIN-" });
    }

    /**
     * Returns the first bluetooth device found that matches one of the given prefixes.
     *
     * @param prefixes Valid prefixes.
     * @return Bluetooth device name.
     */
    private static final String findBluetoothDevice(@NonNull String[] prefixes) {
        String name = null;
        BluetoothAdapter adapter = null;
        Set<BluetoothDevice> bondedDevices = null;

        adapter = BluetoothAdapter.getDefaultAdapter();
        bondedDevices = adapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {
            if (device != null && device.getName() != null) {
                for (String prefix : prefixes) {
                    if (device.getName().startsWith(prefix)) {
                        name = device.getName();
                        break;
                    }
                }
            }
        }

        return name;
    }

}
