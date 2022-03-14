package br.com.uol.pagseguro.plugpagintegradores.ui.list

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.uol.pagseguro.plugpagintegradores.R
import br.com.uol.pagseguro.plugpagintegradores.databinding.ActivityListBluetoothDevicesBinding
import br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth.BluetoothContract
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class ListBluetoothDeviceActivity : AppCompatActivity(), KodeinAware, BluetoothContract.View {

    override val kodein: Kodein by closestKodein()
    private val binding by lazy { ActivityListBluetoothDevicesBinding.inflate(layoutInflater) }
    private val presenter: BluetoothContract.Presenter by instance()

    private lateinit var viewAdapter: BluetoothDeviceListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        viewManager = LinearLayoutManager(
            this
        )
        viewAdapter = BluetoothDeviceListAdapter(
            mutableListOf(),
            ::onBluetoothDeviceSelected
        )
        binding.rvBluetoothDevices.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    override fun onResume() {
        super.onResume()
        presenter.attach(this)
        presenter.loadPairedDevices()
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
    }

    companion object {
        fun getCallingIntent(context: Context): Intent =
            Intent(context, ListBluetoothDeviceActivity::class.java)
    }

    override fun showPairedDevices(devices: List<BluetoothDevice>) {
        viewAdapter.updateDataset(devices)
    }

    override fun showErrorLoadingPairedDevices() {
        Toast
            .makeText(
                this,
                R.string.error_message_selected_bluetooth_device,
                Toast.LENGTH_SHORT
            )
            .show()
    }

    private fun onBluetoothDeviceSelected(device: BluetoothDevice) {
        Toast
            .makeText(
                this,
                getString(R.string.message_selected_bluetooth_device, device.name),
                Toast.LENGTH_SHORT
            )
            .show()
        presenter.saveSelectedBluetoothDevice(device)
    }

}