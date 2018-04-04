package br.com.uol.pagseguro.plugpag.pagcafe.fragment.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.BluetoothDeviceType;

public class BluetoothPairedDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new adapter for bluetooth paired devices.
     *
     * @param deviceList List of paired bluetooth devices.
     */
    public BluetoothPairedDevicesAdapter(Context context, int viewResId, List<BluetoothDevice> deviceList) {
        super(context, viewResId, deviceList);
    }

    // ---------------------------------------------------------------------------------------------
    // View display
    // ---------------------------------------------------------------------------------------------

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        ViewHolder vh = null;

        if (convertView == null) {
            view = LayoutInflater
                    .from(this.getContext())
                    .inflate(R.layout.viewholder_bluetooth_device, parent, false);
        } else {
            view = convertView;
        }

        vh = (ViewHolder) view.getTag();

        if (vh == null) {
            vh = new ViewHolder(view);
            view.setTag(vh);
        }

        this.fillViewHolder(position, vh);

        return view;
    }

    /**
     * Fillsthe ViewHolder.
     *
     * @param position Position of the item used to fill the ViewHolder.
     * @param vh       ViewHolder to be filled.
     */
    private void fillViewHolder(int position, ViewHolder vh) {
        BluetoothDevice device = null;
        String name = null;
        int deviceType = 0;

        device = this.getItem(position);

        if (device != null && vh != null) {
            name = device.getName();

            // Fill device's text data
            vh.name.setText(device.getName());
            vh.macAddress.setText(device.getAddress());

            // Fill device icon
            deviceType = BluetoothDeviceType.getType(name);
            vh.icon.setImageBitmap(null);

            if ((deviceType & BluetoothDeviceType.TYPE_TERMINAL) != 0) {
                if ((deviceType & BluetoothDeviceType.TYPE_PRO) != 0) {
                    vh.icon.setImageResource(R.drawable.device_pro);
                } else if ((deviceType & BluetoothDeviceType.TYPE_WIFI) != 0) {
                    vh.icon.setImageResource(R.drawable.device_wifi);
                }
            } else {
                vh.icon.setImageResource(R.drawable.device_minizinha);
            }

            if (Bluetooth.getSelectedBluetoothDevice() != null &&
                    this.getItem(position).getName().equals(Bluetooth.getSelectedBluetoothDevice().getName())) {
                vh.marker.setBackgroundColor(this.getContext().getResources().getColor(R.color.plugpag_colorPrimaryDark));
            } else {
                vh.marker.setBackgroundColor(this.getContext().getResources().getColor(android.R.color.transparent));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ViewHolder
    // ---------------------------------------------------------------------------------------------

    /**
     * Bluetooth device list's ViewHolder.
     */
    public static class ViewHolder {

        // -----------------------------------------------------------------------------------------
        // Instance attributes
        // -----------------------------------------------------------------------------------------

        public View marker = null;
        public ImageView icon = null;
        public TextView name = null;
        public TextView macAddress = null;

        // -----------------------------------------------------------------------------------------
        // Constructors
        // -----------------------------------------------------------------------------------------

        /**
         * Creates a new Bluetooth device list's ViewHolder.
         *
         * @param view List item View.
         */
        public ViewHolder(View view) {
            this.marker = view.findViewById(R.id.view_marker);
            this.icon = (ImageView) view.findViewById(R.id.img_bluetooth_device_icon);
            this.name = (TextView) view.findViewById(R.id.txt_bluetooth_device_name);
            this.macAddress = (TextView) view.findViewById(R.id.txt_bluetooth_device_mac_address);
        }

    }


}
