package br.com.uol.pagseguro.plugpagintegradores.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import br.com.uol.pagseguro.plugpag.IPlugPag
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagintegradores.R
import br.com.uol.pagseguro.plugpagintegradores.databinding.ActivityPaymentMethodBinding
import br.com.uol.pagseguro.plugpagintegradores.databinding.BottomSheetPaymentBinding
import br.com.uol.pagseguro.plugpagintegradores.extensions.createMaterialDialog
import br.com.uol.pagseguro.plugpagintegradores.extensions.createProgressDialog
import br.com.uol.pagseguro.plugpagintegradores.extensions.removeFormat
import br.com.uol.pagseguro.plugpagintegradores.extensions.toCurrency
import br.com.uol.pagseguro.plugpagintegradores.presentation.payment.PaymentContract
import br.com.uol.pagseguro.plugpagintegradores.ui.base.BaseActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.list.ListTransactionActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.kodein.di.generic.instance

class PaymentMethodActivity : BaseActivity(), PaymentContract.View,
    View.OnClickListener {

    private val binding by lazy { ActivityPaymentMethodBinding.inflate(layoutInflater) }
    private val bindingBottomSheet by lazy { BottomSheetPaymentBinding.inflate(layoutInflater) }
    private val presenter: PaymentContract.Presenter by instance()
    private lateinit var bottomSheet: BottomSheetDialog
    private val dialog by lazy { createProgressDialog() }

    private var isQrcode = false
    private var isPix = false

    companion object {
        fun getCallingIntent(context: Context): Intent =
            Intent(context, PaymentMethodActivity::class.java)

        private const val DEBIT_TYPE_SELECTION = 0
        private const val CREDIT_TYPE_SELECTION = 1
        private const val INSTALLMENT_1X_SELECTION = 0
        private const val INSTALLMENT_TYPE_SELLER_SELECTION = 0
        private const val INSTALLMENT_TYPE_VENDEDOR_SELECTION = 1

        private const val INSTALLMENT_TYPE_A_VISTA = 1
        private const val INSTALLMENT_TYPE_PARC_VENDEDOR = 2
        private const val INSTALLMENT_TYPE_PARC_COMPRADOR = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpBottomSheet()
        setUpTextWacherValuePayment()
        setUpInicialAmount()
    }

    private fun setUpInicialAmount() {
        binding.valueField.setText(0.0.toCurrency())
    }

    override fun onResume() {
        super.onResume()
        presenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
    }

    private fun setUpTextWacherValuePayment() {
        binding.valueField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val valueDigit = charSequence
                    .toString()
                    .removeFormat()

                if (TextUtils.isEmpty(valueDigit)) {
                    binding.valueField.setText("0")
                } else {
                    binding.valueField.removeTextChangedListener(this)
                    val converted: Double = valueDigit.toDouble() / 100
                    binding.valueField.setText(converted.toCurrency())
                    binding.valueField.setSelection(converted.toCurrency().length)
                    binding.valueField.addTextChangedListener(this)
                }
            }
        })
    }

    private fun setUpBottomSheet() {
        bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(bindingBottomSheet.root)
    }

    override fun onClick(itemViewSelected: View?) {
        resetBottomSheet()
        when (itemViewSelected?.id) {
            R.id.qrcode_button -> qrCodePayment()
            R.id.credit_button -> creditPayment()
            R.id.debit_button -> debitPayment()
            R.id.voucher_button -> voucherPayment()
            R.id.pix_button -> pixPayment()
            R.id.estorno_button -> startActivity(
                ListTransactionActivity.getCallingIntent(this)
                    .putExtra("estorno", true)
            )
            else -> dismissBottomSheet()
        }
        bindingBottomSheet.btnPay.setOnClickListener {
            onClickBtnPay()
        }
    }

    private fun resetBottomSheet() {
        bindingBottomSheet.bottomSheetQrcode.visibility = View.GONE
        bindingBottomSheet.spnInstallmentsAmount.setSelection(INSTALLMENT_1X_SELECTION)
        bindingBottomSheet.spnTypeSale.setSelection(DEBIT_TYPE_SELECTION)
        bindingBottomSheet.spnInstallmentType.setSelection(INSTALLMENT_TYPE_SELLER_SELECTION)
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
                        R.string.transaction_sucess,
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

    //Payment Methods

    private fun qrCodePayment() {
        showQRPaymentType()
        isQrcode = true
        bindingBottomSheet.titleBsView.text = getString(R.string.qrcode)
        bindingBottomSheet.spnTypeSale.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) = hideInstallments()

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                when (bindingBottomSheet.spnTypeSale.selectedItemPosition) {
                    0 -> hideInstallments()
                    else -> showInstallments()
                }
            }
        }
        bottomSheet.show()
    }

    private fun debitPayment() {
        bindingBottomSheet.spnTypeSale.setSelection(DEBIT_TYPE_SELECTION)
        hideQRPaymentType()
        onClickBtnPay()
    }

    private fun voucherPayment() {
        binding.valueField.text.toString().removeFormat().apply {
            doPay(
                amount = this.toInt(),
                installments = 1,
                installmentType = INSTALLMENT_TYPE_A_VISTA,
                paymentType = IPlugPag.TYPE_VOUCHER
            )
        }
    }

    private fun pixPayment() {
        isPix = true
        bindingBottomSheet.spnTypeSale.setSelection(DEBIT_TYPE_SELECTION)
        hideQRPaymentType()
        onClickBtnPay()
    }

    private fun creditPayment() {
        bindingBottomSheet.titleBsView.text = getString(R.string.credito)
        bindingBottomSheet.spnTypeSale.setSelection(CREDIT_TYPE_SELECTION)
        hideQRPaymentType()
        showInstallments()
        bottomSheet.show()
    }

    private fun onClickBtnPay() {
        dismissBottomSheet()
        try {
            binding.valueField.text.toString().removeFormat().apply {
                doPay(
                    amount = this.toInt(),
                    installments = getInstallmentNumber(),
                    installmentType = getInstallmentType(),
                    paymentType = getSaleType()
                )
            }
        } catch (e: Exception) {
            showToast(getText(R.string.invalidValue) as String)
        }
        isQrcode = false
        isPix = false
    }

    private fun doPay(amount: Int, paymentType: Int, installmentType: Int, installments: Int) {
        Log.i(
            "transaction", "Transacao: ${convertPaymentTypeToText(paymentType)} " +
                    "Tipo: ${convertInstallmentTypeToText(installmentType)} " +
                    "Valor: $amount " +
                    "Parcelas: $installments"
        )

        presenter.pay(
            amount,
            paymentType,
            installmentType,
            installments
        )
    }

    private fun convertPaymentTypeToText(paymentType: Int) = when (paymentType) {
        IPlugPag.TYPE_CREDITO -> getString(R.string.credito)
        IPlugPag.TYPE_DEBITO -> getString(R.string.debito)
        IPlugPag.TYPE_VOUCHER -> getString(R.string.voucher)
        IPlugPag.TYPE_QRCODE_ELO_DEBITO ->
            "${getString(R.string.qrcode)} ${getString(R.string.debito)}"
        IPlugPag.TYPE_QRCODE_ELO_CREDITO ->
            "${getString(R.string.qrcode)} ${getString(R.string.credito)}"
        IPlugPag.TYPE_PIX -> "${getString(R.string.qrcode)} ${getString(R.string.pix)}"
        else -> "Qual tipo?"
    }

    private fun convertInstallmentTypeToText(installmentType: Int) = when (installmentType) {
        INSTALLMENT_TYPE_PARC_VENDEDOR -> getString(R.string.vendedor)
        INSTALLMENT_TYPE_PARC_COMPRADOR -> getString(R.string.comprador)
        INSTALLMENT_TYPE_A_VISTA -> getString(R.string.avista)
        else -> "Nova modalidade?"
    }

    private fun dismissBottomSheet() {
        hideInstallments()
        if (bottomSheet.isShowing) {
            bottomSheet.dismiss()
            bottomSheet.cancel()
        }
    }

    private fun getInstallmentNumber() =
        bindingBottomSheet.spnInstallmentsAmount.selectedItemPosition + 1

    private fun getInstallmentType() =
        when (bindingBottomSheet.spnInstallmentType.selectedItem) {
            getString(R.string.vendedor) -> {
                INSTALLMENT_TYPE_PARC_VENDEDOR
            }
            getString(R.string.comprador) -> {
                INSTALLMENT_TYPE_PARC_COMPRADOR
            }
            else -> {
                INSTALLMENT_TYPE_A_VISTA
            }
        }

    private fun getSaleType() = if (isQrcode) {
        if (bindingBottomSheet.spnTypeSale.selectedItem == getString(R.string.debito)) {
            IPlugPag.TYPE_QRCODE_ELO_DEBITO
        } else {
            IPlugPag.TYPE_QRCODE_ELO_CREDITO
        }
    } else if (isPix) {
        IPlugPag.TYPE_PIX
    } else {
        if (bindingBottomSheet.spnTypeSale.selectedItem == getString(R.string.debito)) {
            IPlugPag.TYPE_DEBITO
        } else {
            IPlugPag.TYPE_CREDITO
        }
    }

    override fun showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    // Show/Hide Components Views

    fun showQRPaymentType() {
        bindingBottomSheet.bottomSheetQrcode.visibility = View.VISIBLE
    }

    fun hideQRPaymentType() {
        bindingBottomSheet.bottomSheetQrcode.visibility = View.GONE
    }

    fun showInstallmentsType() {
        bindingBottomSheet.spnInstallmentType.visibility = View.VISIBLE
        bindingBottomSheet.spnInstallmentType.setSelection(
            INSTALLMENT_TYPE_VENDEDOR_SELECTION
        )
    }

    fun hideInstallmentsType() {
        bindingBottomSheet.spnInstallmentType.visibility = View.INVISIBLE
        bindingBottomSheet.spnInstallmentType.setSelection(
            INSTALLMENT_TYPE_SELLER_SELECTION
        )
    }

    fun showInstallments() {
        bindingBottomSheet.txtLabelMethod.visibility = View.VISIBLE
        bindingBottomSheet.spnInstallmentsAmount.visibility = View.VISIBLE
        bindingBottomSheet.spnInstallmentsAmount.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    bindingBottomSheet.spnInstallmentsAmount.setSelection(INSTALLMENT_1X_SELECTION)
                    bindingBottomSheet.spnInstallmentType.visibility = View.INVISIBLE
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    if (bindingBottomSheet.spnInstallmentsAmount.selectedItem == getString(R.string.inCash)) {
                        hideInstallmentsType()
                    } else {
                        showInstallmentsType()
                    }
                }
            }
    }

    fun hideInstallments() {
        bindingBottomSheet.txtLabelMethod.visibility = View.GONE
        bindingBottomSheet.spnInstallmentType.visibility = View.GONE
        bindingBottomSheet.spnInstallmentsAmount.visibility = View.GONE
    }
}