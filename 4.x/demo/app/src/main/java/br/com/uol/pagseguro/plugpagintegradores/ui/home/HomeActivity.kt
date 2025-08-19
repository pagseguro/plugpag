package br.com.uol.pagseguro.plugpagintegradores.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import br.com.uol.pagseguro.plugpagintegradores.R
import br.com.uol.pagseguro.plugpagintegradores.databinding.ActivityHomeBinding
import br.com.uol.pagseguro.plugpagintegradores.extensions.createProgressDialog
import br.com.uol.pagseguro.plugpagintegradores.extensions.showToast
import br.com.uol.pagseguro.plugpagintegradores.presentation.auth.AuthenticationContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.permission.PermissionContract
import br.com.uol.pagseguro.plugpagintegradores.ui.base.BaseActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.list.ListBluetoothDeviceActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.list.ListTransactionActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.payment.PaymentMethodActivity
import org.kodein.di.generic.instance

class HomeActivity :
    BaseActivity(),
    PermissionContract.View,
    AuthenticationContract.View {

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 0x1234
        const val REFUND_TAG = "estorno"
    }

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val presenterAuthentication: AuthenticationContract.Presenter by instance()
    private val presenterPermission: PermissionContract.Presenter by instance()
    private val dialog by lazy { createProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
        presenterPermission.bind(this)
        presenterPermission.requestPermissions(this, PERMISSIONS_REQUEST_CODE)
        presenterAuthentication.attach(this)
    }

    override fun onStart() {
        super.onStart()
        presenterAuthentication.checkAuthentication()
        presenterAuthentication.checkBluetoothDevice()
    }

    override fun onDestroy() {
        presenterAuthentication.detach()
        presenterPermission.unbind()
        super.onDestroy()
    }

    private fun insertListeners() {
        binding.pagamentosButton.setOnClickListener {
            startActivity(
                PaymentMethodActivity.getCallingIntent(this)
            )
        }

        binding.bluetoothButton.setOnClickListener {
            startActivity(
                ListBluetoothDeviceActivity.getCallingIntent(this)
            )
        }

        binding.relatorioButton.setOnClickListener {
            startActivity(
                ListTransactionActivity.getCallingIntent(this)
                    .putExtra(REFUND_TAG, false)
            )
        }

        binding.autenticacaoButton.setOnClickListener {
            presenterAuthentication.requestAuthentication()
        }

        binding.removerAutenticacaoButton.setOnClickListener {
            presenterAuthentication.invalidateAuthentication()
        }
    }

    override fun requestPermissions(permissions: Array<String>) {
        permissions.forEach { _ ->
            ActivityCompat.requestPermissions(
                this,
                permissions,
                0
            )
        }
    }

    override fun showLoadingAnimation() {
        dialog.show()
    }

    override fun hideLoadingAnimation() {
        dialog.dismiss()
    }

    override fun showAuthenticatedSession() {
        binding.autenticacaoButton.visibility = View.GONE
        binding.removerAutenticacaoButton.visibility = View.VISIBLE
        showMessage(getString(R.string.authentication_success))
    }

    override fun showMissingAuthenticationView() {
        binding.autenticacaoButton.visibility = View.VISIBLE
        binding.removerAutenticacaoButton.visibility = View.GONE
        showMessage(getString(R.string.authentication_failed))
    }

    override fun showMessage(message: String) {
        showToast(message)
    }
}
