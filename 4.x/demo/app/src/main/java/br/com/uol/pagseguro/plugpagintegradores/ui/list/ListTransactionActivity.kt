package br.com.uol.pagseguro.plugpagintegradores.ui.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.uol.pagseguro.libswitch.comm.BComp.context
import br.com.uol.pagseguro.plugpag.PlugPagDevice
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagintegradores.R
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary
import br.com.uol.pagseguro.plugpagintegradores.databinding.ActivityListTransactionBinding
import br.com.uol.pagseguro.plugpagintegradores.extensions.createMaterialDialog
import br.com.uol.pagseguro.plugpagintegradores.extensions.createProgressDialog
import br.com.uol.pagseguro.plugpagintegradores.presentation.payment.PaymentContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.transaction.ListTransactionContract
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class ListTransactionActivity : ListTransactionContract.View, PaymentContract.View, AppCompatActivity(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private val binding by lazy { ActivityListTransactionBinding.inflate(layoutInflater) }
    private val presenter : ListTransactionContract.Presenter by instance()
    private val presenterPayment: PaymentContract.Presenter by instance()
    private val dialog by lazy { createProgressDialog() }

    private lateinit var viewAdapter: TransactionItemListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val estorno : Boolean by lazy {
        intent.getBooleanExtra("estorno",false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
    }

    companion object {
        fun getCallingIntent(context: Context): Intent =
            Intent(context, ListTransactionActivity::class.java)
    }

    private fun initViews() {
        viewManager = LinearLayoutManager(context)
        viewAdapter = TransactionItemListAdapter(mutableListOf(), ::onTransactionSelected)

        binding.transactionList.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attach(this)
        presenterPayment.attach(this)
        presenter.loadTransactions()
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
        presenterPayment.detach()
    }

    private fun onTransactionSelected(transaction: TransactionSummary) {
        if (estorno) {
            presenter.startVoidPayment(transaction)
        }
    }

    override fun showTransactions(transactions: List<TransactionSummary>) {
        viewAdapter.updateDataset(transactions)
        viewAdapter.notifyDataSetChanged()
    }

    override fun showVoidTransaction(device: PlugPagDevice, transaction: TransactionSummary, position: Int) {
        dialog.show()
        presenterPayment.voidPayment(device, transaction, position)
        viewAdapter.notifyItemRemoved(position);
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showLoading() {
        dialog.show()
    }

    override fun disableLoading() {
        dialog.dismiss()
    }

    override fun showText(text: String) {
        dialog.setMessage(text)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showTransactionResult(result: PlugPagTransactionResult?) {
        result?.let {
            dialog.dismiss()
            createMaterialDialog {
                setMessage(
                    getString(
                        R.string.estorno_sucess,
                        result.errorCode,
                        result.message,
                        result.transactionId,
                        result.transactionCode
                    )
                )
            }.show()
        } ?: run {
            createMaterialDialog {
                setMessage(R.string.transaction_not_realized)
            }.show()
        }
    }
}