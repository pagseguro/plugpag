package br.com.uol.pagseguro.plugpagintegradores.ui.list

import android.bluetooth.BluetoothDevice
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.uol.pagseguro.plugpagintegradores.R

class BluetoothDeviceListAdapter(
    private val dataset: MutableList<BluetoothDevice>,
    private val itemClickListener: (BluetoothDevice) -> Unit
) :
    RecyclerView.Adapter<BluetoothDeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.bluetooth_device_item, parent, false)

        return ViewHolder(root)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataset[position].name
        holder.macaddress.text = dataset[position].address
        holder.itemView.setOnClickListener { itemClickListener(dataset[position]) }
    }

    fun updateDataset(dataset: List<BluetoothDevice>) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        this.notifyDataSetChanged()
    }

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val name: AppCompatTextView = root.findViewById(R.id.name)
        val macaddress: AppCompatTextView = root.findViewById(R.id.macaddress)
    }

}