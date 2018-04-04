package br.com.uol.pagseguro.plugpag.pagcafe.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public final class BluetoothDeviceType {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    private static final String PREFIX_PRO = "PRO-";
    private static final String PREFIX_WIFI = "W-";
    private static final String PREFIX_WIFI_PLUS = "W+-";
    private static final String PREFIX_MINIZINHA = "PAX-";
    private static final String PREFIX_MOBI = "MOBI-";

    public static final int TYPE_TERMINAL = 0x10000000;
    public static final int TYPE_PINPAD = 0x20000000;

    public static final int TYPE_PRO = 0x00000010 | BluetoothDeviceType.TYPE_TERMINAL;
    public static final int TYPE_WIFI = 0x00000020 | BluetoothDeviceType.TYPE_TERMINAL;
    public static final int TYPE_MINIZINHA = 0x00000010 | BluetoothDeviceType.TYPE_PINPAD;
    public static final int TYPE_MOBI = 0x00000020 | BluetoothDeviceType.TYPE_PINPAD;

    // ---------------------------------------------------------------------------------------------
    // Validation methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a device's type.
     *
     * @param deviceName Device name to be evaluated to a type.
     * @return Device type.
     */
    public static final int getType(@NonNull String deviceName) {
        int type = 0;

        if (deviceName != null && deviceName.length() > 0) {
            if (deviceName.startsWith(BluetoothDeviceType.PREFIX_PRO)) {
                type = BluetoothDeviceType.TYPE_PRO;
            } else if (deviceName.startsWith(BluetoothDeviceType.PREFIX_WIFI) ||
                    deviceName.startsWith(BluetoothDeviceType.PREFIX_WIFI_PLUS)) {
                type = BluetoothDeviceType.TYPE_WIFI;
            } else if (deviceName.startsWith(BluetoothDeviceType.PREFIX_MINIZINHA)) {
                type = BluetoothDeviceType.TYPE_MINIZINHA;
            } else if (deviceName.startsWith(BluetoothDeviceType.PREFIX_MOBI)) {
                type = BluetoothDeviceType.TYPE_MOBI;
            }
        }

        return type;
    }

    /**
     * Checks if a device is a terminal.
     *
     * @param deviceName Name of the device to be checked.
     * @return If the device is a terminal.
     */
    public static final boolean isTerminal(@NonNull String deviceName) {
        boolean isTerminal = false;

        if (deviceName != null && deviceName.length() > 0) {
            if (deviceName.startsWith(BluetoothDeviceType.PREFIX_PRO) ||
                    deviceName.startsWith(BluetoothDeviceType.PREFIX_WIFI) ||
                    deviceName.startsWith(BluetoothDeviceType.PREFIX_WIFI_PLUS)) {
                isTerminal = true;
            }
        }

        return isTerminal;
    }

    /**
     * Checks if a device is a terminal.
     *
     * @param device Device to be checked.
     * @return If the device is a terminal.
     */
    public static final boolean isTerminal(@NonNull BluetoothDevice device) {
        boolean isTerminal = false;

        if (device != null) {
            isTerminal = BluetoothDeviceType.isTerminal(device.getName());
        }

        return isTerminal;
    }

    /**
     * Checks if a device is a pinpad.
     *
     * @param deviceName Name of the device to be checked.
     * @return If the device is a pinpad.
     */
    public static final boolean isPinpad(@NonNull String deviceName) {
        boolean isPinpad = false;

        if (deviceName != null && deviceName.length() > 0) {
            isPinpad = !BluetoothDeviceType.isTerminal(deviceName);
        }

        return isPinpad;
    }

    /**
     * Checks if a device is a pinpad.
     *
     * @param device Device to be checked.
     * @return If the device is a pinpad.
     */
    public static final boolean isPinpad(@NonNull BluetoothDevice device) {
        boolean isPinpad = false;

        if (device != null) {
            isPinpad = BluetoothDeviceType.isPinpad(device.getName());
        }

        return isPinpad;
    }

}
