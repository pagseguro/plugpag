package br.com.uol.pagseguro.plugpagintegradores.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import br.com.uol.pagseguro.plugpagintegradores.R
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary
import java.lang.StringBuilder
import java.text.DecimalFormat

class TransactionItemListAdapter(
    private val dataset: MutableList<TransactionSummary>,
    private val itemClickListener: (TransactionSummary) -> Unit
) : RecyclerView.Adapter<TransactionItemListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)

        return ViewHolder(root)
    }

    override fun getItemCount() = dataset.size

    private fun formatCurrency(value: String) =
        DecimalFormat.getCurrencyInstance().format(value.toInt() / 100.0).toString()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.datetime.text = StringBuilder()
            .append(dataset[position].date)
            .append(" - ")
            .append(dataset[position].time)
        holder.value.text = formatCurrency(dataset[position].value)
        holder.transactionId.text = dataset[position].transactionId
        holder.transactionCode.text = dataset[position].transactionCode
        holder.cardBrand.text = dataset[position].cardBrand
        holder.itemView.setOnClickListener { itemClickListener(dataset[position]) }
    }

    fun updateDataset(dataset: List<TransactionSummary>) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
    }

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val datetime: AppCompatTextView = root.findViewById(R.id.datetime)
        val value: AppCompatTextView = root.findViewById(R.id.value)
        val transactionId: AppCompatTextView = root.findViewById(R.id.transactionId)
        val transactionCode: AppCompatTextView = root.findViewById(R.id.transactionCode)
        val cardBrand: AppCompatTextView = root.findViewById(R.id.cardBrand)
    }

}