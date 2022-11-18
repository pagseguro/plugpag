package br.com.uol.pagseguro.plugpagintegradores.presentation.payment

import android.os.Bundle
import android.util.Log
import br.com.uol.pagseguro.plugpag.PlugPag
import br.com.uol.pagseguro.plugpag.PlugPagDevice
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpag.PlugPagVoidData
import br.com.uol.pagseguro.plugpagintegradores.data.local.DataStorageContract
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary
import br.com.uol.pagseguro.plugpagintegradores.presentation.log.LoggerContract
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.random.Random

class PaymentPresenter(
    private val storage: DataStorageContract,
    private val plugpag: PlugPag,
    private val logger: LoggerContract
) : PaymentContract.Presenter {

    companion object {
        private val LIST_LESS_DEVICE = arrayOf(
            PlugPag.TYPE_QRCODE_ELO_DEBITO,
            PlugPag.TYPE_PIX,
            PlugPag.TYPE_QRCODE_ELO_CREDITO
        )
    }

    private var view: PaymentContract.View? = null
    private val disposables = CompositeDisposable()
    private var paymentValue = 0L
    private var lastDeviceName: String? = null
    private var lastTransactionCode: String? = null

    override fun attach(view: PaymentContract.View) {
        this.view = view
        paymentValue = 0L
    }

    override fun detach() {
        view = null
    }

    override fun pay(
        value: Int,
        paymentType: Int,
        installmentType: Int,
        installments: Int
    ) {
        view?.showLoading()

        disposables.add(
            Observable
            .create<Bundle> { emitter ->
                val device = PlugPagDevice(storage.getSelectedBluetoothDevice())
                val paymentData = PlugPagPaymentData(
                    paymentType,
                    value,
                    installmentType,
                    installments,
                    "teste"
                )

                val initBtConnection = plugpag.initBTConnection(
                    PlugPagDevice(
                        device.identification,
                        null,
                        null,
                        paymentData.type in LIST_LESS_DEVICE
                    )
                )

                Log.d("initBTConnection ->", " $initBtConnection")

                lastDeviceName = device.identification

                plugpag.setEventListener { eventData ->
                    emitter.onNext(Bundle().apply {
                        putInt(PaymentContract.PP_CODE, eventData.eventCode)
                        putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                    })

                    PlugPag.RET_OK
                }
                plugpag.setNativeEventListener { eventData ->
                    emitter.onNext(Bundle().apply {
                        putInt(PaymentContract.PP_CODE, eventData.eventCode)
                        putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                    })

                    PlugPag.RET_OK
                }

                val result = plugpag.doPayment(paymentData)

                if (result.result == PlugPag.RET_OK) {
                    lastTransactionCode = result.transactionCode
                    storage.saveTransactionResult(result)
                }

                emitter.onNext(Bundle().apply {
                    putSerializable(PaymentContract.PP_RESULT, result)
                })
                emitter.onComplete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.containsKey(PaymentContract.PP_MESSAGE)) {
                        view?.showText(it.getString(PaymentContract.PP_MESSAGE)!!)
                    }

                    if (it.containsKey(PaymentContract.PP_RESULT)) {
                        view?.showTransactionResult(
                            it.getSerializable(
                                PaymentContract.PP_RESULT
                            ) as PlugPagTransactionResult
                        )
                    }
                },

                onComplete = {
                    view?.disableLoading()
                },

                onError = {
                    view?.apply {
                        showTransactionResult(null)
                        disableLoading()
                        showText(it.localizedMessage)
                    }
                }
            ))
    }

    override fun voidPayment(
        device: PlugPagDevice,
        transaction: TransactionSummary,
        position: Int
    ) {
        disposables.add(Observable
            .create<Bundle> { emitter ->
                plugpag.initBTConnection(
                    PlugPagDevice(
                        device.identification,
                        null,
                        null,
                        transaction.method in LIST_LESS_DEVICE
                    )
                )
                lastDeviceName = device.identification
                plugpag.setEventListener { eventData ->
                    emitter.onNext(Bundle().apply {
                        putInt(PaymentContract.PP_CODE, eventData.eventCode)
                        putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                    })
                    PlugPag.RET_OK
                }

                plugpag.setEventListener { eventData ->
                    emitter.onNext(Bundle().apply {
                        putInt(PaymentContract.PP_CODE, eventData.eventCode)
                        putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                    })

                    PlugPag.RET_OK
                }

                plugpag.setNativeEventListener { eventData ->
                    emitter.onNext(Bundle().apply {
                        putInt(PaymentContract.PP_CODE, eventData.eventCode)
                        putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                    })

                    PlugPag.RET_OK
                }

                val isQrCode = transaction.method == PlugPag.TYPE_QRCODE_ELO_CREDITO ||
                        transaction.method == PlugPag.TYPE_QRCODE_ELO_DEBITO

                val transactionMethod = PlugPagVoidData.TypeCardTransaction.getBy(transaction.method)
                val paymentData = PlugPagVoidData(transaction.transactionCode, transaction.transactionId, transactionMethod)

                val result = if (isQrCode) {
                    plugpag.voidQRCodePayment(paymentData)
                } else {
                    plugpag.voidPayment(paymentData)
                }

                if (result.result == PlugPag.RET_OK) {
                    storage.deleteTransaction(position)
                }

                emitter.onNext(Bundle().apply {
                    putSerializable(PaymentContract.PP_RESULT, result)
                })
                emitter.onComplete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.containsKey(PaymentContract.PP_MESSAGE)) {
                        view?.showText(it?.getString(PaymentContract.PP_MESSAGE) ?: "")
                    }

                    if (it.containsKey(PaymentContract.PP_RESULT)) {
                        view?.showTransactionResult(it.getSerializable(PaymentContract.PP_RESULT) as PlugPagTransactionResult)
                    }
                },

                onComplete = {
                    logger.debug(this, "Estorno Finalizado")
                    view?.disableLoading()
                },

                onError = {
                    logger.error(it.localizedMessage)
                    view?.apply {
                        disableLoading()
                        showText(it.localizedMessage)
                    }
                }
            ))
    }

    override fun sendReceiptSMS(phoneNumber: String) {
        if (phoneNumber.isNotBlank() && lastTransactionCode != null) {
            disposables.add(Observable
                .create<Bundle> { emitter ->
                    plugpag.setEventListener { eventData ->
                        emitter.onNext(Bundle().apply {
                            putInt(PaymentContract.PP_CODE, eventData.eventCode)
                            putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                        })

                        PlugPag.RET_OK
                    }
                    plugpag.setNativeEventListener { eventData ->
                        emitter.onNext(Bundle().apply {
                            putInt(PaymentContract.PP_CODE, eventData.eventCode)
                            putString(PaymentContract.PP_MESSAGE, eventData.customMessage)
                        })

                        PlugPag.RET_OK
                    }

                    val result = plugpag.sendReceiptToSMS(phoneNumber, lastTransactionCode)

                    lastTransactionCode = null

                    emitter.onNext(Bundle().apply {
                        putSerializable(PaymentContract.PP_RESULT, result)
                    })
                    emitter.onComplete()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        if (it.containsKey(PaymentContract.PP_MESSAGE)) {
                            view?.showText(it.getString(PaymentContract.PP_MESSAGE)!!)
                        }

                    },

                    onComplete = {
                        Log.d("Send sms", "Finished")
                    },

                    onError = {
                        Log.d("Finished send sms", "Error")
                    }
                ))
        }
    }

    private fun getDevice(less: Boolean): PlugPagDevice {
        val deviceName = storage.getSelectedBluetoothDevice()
        return PlugPagDevice(deviceName, null, null, less)
    }

}
