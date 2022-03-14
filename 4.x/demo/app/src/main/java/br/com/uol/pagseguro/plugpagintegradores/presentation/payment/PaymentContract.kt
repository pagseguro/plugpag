package br.com.uol.pagseguro.plugpagintegradores.presentation.payment

import br.com.uol.pagseguro.plugpag.PlugPagDevice
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagintegradores.data.model.TransactionSummary

interface PaymentContract {

    companion object {
        const val KEY_PAYMENT_VALUE = "KEY_PAMEYMENT_VALUE"
        const val KEY_PAYMENT_METHOD = "KEY_PAYMENT_METHOD"
        const val KEY_INSTALLMENT_TYPE = "KEY_INSTALLMENT_TYPE"
        const val KEY_INSTALLMENTS = "KEY_INSTALLMENTS"

        const val OPERATION = "OPERATION"
        const val OPERATION_PAYMENT = 1
        const val OPERATION_VOID = 2
        const val DEVICE_NAME = "DEVICE_NAME"
        const val PAYMENT_VALUE = "PAYMENT_VALUE"
        const val PAYMENT_METHOD = "PAYMENT_METHOD"
        const val PAYMENT_INSTALLMENT_TYPE = "PAYMENT_INSTALLMENT_TYPE"
        const val PAYMENT_INSTALLMENTS = "PAYMENT_INSTALLMENTS"
        const val PP_MESSAGE = "PP_MESSAGE"
        const val PP_CODE = "PP_CODE"
        const val PP_RESULT = "PP_RESULT"
        const val TRANSACTION_POSITION = "TRANSACTION_POSITION"
        const val TRANSACTION_ID = "TRANSACTION_ID"
        const val TRANSACTION_CODE = "TRANSACTION_CODE"
    }


    interface View {
        fun showToast(message: String)
        fun showLoading()
        fun disableLoading()

        fun showText(text: String)
        fun showTransactionResult(result: PlugPagTransactionResult?)

    }

    interface Presenter {
        fun attach(view: View)
        fun detach()

        fun pay(
            value: Int,
            paymentType: Int,
            installmentType: Int,
            installments: Int
        )

        fun voidPayment(
            device: PlugPagDevice,
            transaction: TransactionSummary,
            position: Int
        )

        fun sendReceiptSMS(phoneNumber: String)

    }

}