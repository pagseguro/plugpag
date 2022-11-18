package br.com.uol.pagseguro.plugpagintegradores.presentation.transaction

import br.com.uol.pagseguro.plugpag.PlugPagDevice
import br.com.uol.pagseguro.plugpagintegradores.data.local.DataStorageContract
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary
import br.com.uol.pagseguro.plugpagintegradores.presentation.log.LoggerContract
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ListTransactionPresenter(
    private val logger: LoggerContract,
    private val storage: DataStorageContract
) : ListTransactionContract.Presenter {

    private val disposables = CompositeDisposable()
    private var view : ListTransactionContract.View? = null

    override fun attach(view: ListTransactionContract.View) {
        logger.debug(this, "Attaching to ${view::class.java}")
        this.view = view
    }

    override fun detach() {
        logger.debug(this, "Clearing disposables")
        disposables.clear()

        logger.debug(this, "Detaching from ${view!!::class.java}")
        view = null
    }

    override fun loadTransactions() {
        disposables.add(
            Single
                .create<List<TransactionSummary>> { emitter ->
                    try {
                        val count = storage.getTransactionCount()
                        val transactions = mutableListOf<TransactionSummary>()

                        (1..count).forEach {
                            storage.getTransaction(it)?.let { transaction ->
                                transactions.add(transaction)
                            }
                        }
                        emitter.onSuccess(transactions)
                    } catch (e: Exception) {
                        emitter.tryOnError(e)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        view?.showTransactions(it)
                    },

                    onError = {
                        logger.error("Erro ao carregar lista de transações")
                    }
                ))
    }

    override fun startVoidPayment(transaction: TransactionSummary) {
        disposables.add(Single
            .create<Pair<PlugPagDevice, TransactionSummary>> { emitter ->
                val device = PlugPagDevice(storage.getSelectedBluetoothDevice())
                emitter.onSuccess(Pair(device, transaction))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    //todo: implementar chamada de estorno
                    view?.showVoidTransaction(it.first, it.second, transaction.id)
                },

                onError = {
                    logger.debug("Erro ao estornar")
                }
            ))
    }
}