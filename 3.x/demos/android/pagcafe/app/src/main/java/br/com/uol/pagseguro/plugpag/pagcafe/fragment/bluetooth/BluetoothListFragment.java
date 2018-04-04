package br.com.uol.pagseguro.plugpag.pagcafe.fragment.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.R;

public final class BluetoothListFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private ProgressBar mProgressBar = null;
    private ListView mDevicesListView = null;

    private List<BluetoothDevice> mBluetoothDevices = null;
    private BluetoothPairedDevicesAdapter mAdapter = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new Fragment.
     */
    public BluetoothListFragment() { }

    // ---------------------------------------------------------------------------------------------
    // Fragment creator
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of the BluetoothListFragment.
     *
     * @return New instance of the BluetoothListFragment.
     */
    public static final BluetoothListFragment newInstance() {
        BluetoothListFragment fragment = null;

        fragment = new BluetoothListFragment();

        return fragment;
    }

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bluetooth_list, container, false);

        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mProgressBar = (ProgressBar) view.findViewById(R.id.pgb_loading_bluetooth);
        this.mDevicesListView = (ListView) view.findViewById(R.id.lst_paired_devices);
        this.mBluetoothDevices = new ArrayList<>();

        this.mDevicesListView.setOnItemClickListener(this);
        this.setupBluetoothDeviceList();
        this.loadBluetoothPairedDevices();
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth device list setup
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups the bluetooth device RecyclerView.
     */
    private void setupBluetoothDeviceList() {
        this.mAdapter = new BluetoothPairedDevicesAdapter(
                this.getContext(), R.layout.viewholder_bluetooth_device, this.mBluetoothDevices);
        this.mDevicesListView.setAdapter(this.mAdapter);
    }

    // ---------------------------------------------------------------------------------------------
    // Load bluetooth paired devices
    // ---------------------------------------------------------------------------------------------

    /**
     * Loads the list of bluetooth paired devices.
     */
    private void loadBluetoothPairedDevices() {
        BluetoothAdapter adapter = null;
        List<BluetoothDevice> pairedDevices = null;

        // Show the progressbar
        this.mProgressBar.setVisibility(View.VISIBLE);

        // Load paired devices
        adapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = this.filterPagSeguroBluetoothDevices(adapter.getBondedDevices());

        this.mBluetoothDevices.clear();
        this.mBluetoothDevices.addAll(pairedDevices);
        this.mAdapter.notifyDataSetChanged();

        // Hide the progressbar
        this.mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Filters the devices that can be used.
     *
     * @param devices List of devices to be filtered.
     * @return List of filtered devices.
     */
    private List<BluetoothDevice> filterPagSeguroBluetoothDevices(Set<BluetoothDevice> devices) {
        List<BluetoothDevice> filtered = null;

        filtered = new ArrayList<>();

        if (devices != null) {
            for (BluetoothDevice device : devices) {
                if (device.getName().startsWith("PRO-") ||
                        device.getName().startsWith("W-") ||
                        device.getName().startsWith("W+-") ||
                        device.getName().startsWith("MOBI-") ||
                        device.getName().startsWith("PAX-") ||
                        device.getName().startsWith("PLUS-")) {
                    filtered.add(device);
                }
            }
        }

        return filtered;
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth device selection
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0 && position < this.mBluetoothDevices.size()) {
            Bluetooth.setSelectedBluetoothDevice(this.mBluetoothDevices.get(position));
        }

        this.dismiss();
    }
}
