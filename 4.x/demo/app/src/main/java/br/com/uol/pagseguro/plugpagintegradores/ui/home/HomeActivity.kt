package br.com.uol.pagseguro.plugpagintegradores.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import br.com.uol.pagseguro.plugpagintegradores.R
import br.com.uol.pagseguro.plugpagintegradores.databinding.ActivityHomeBinding
import br.com.uol.pagseguro.plugpagintegradores.extensions.createProgressDialog
import br.com.uol.pagseguro.plugpagintegradores.presentation.auth.AuthenticationContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.permission.PermissionContract
import br.com.uol.pagseguro.plugpagintegradores.ui.base.BaseActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.list.ListBluetoothDeviceActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.list.ListTransactionActivity
import br.com.uol.pagseguro.plugpagintegradores.ui.payment.PaymentMethodActivity
import org.kodein.di.generic.instance

class HomeActivity : BaseActivity(),
    PermissionContract.View,
    AuthenticationContract.View {

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 0x1234
    }

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val presenterAuthentication: AuthenticationContract.Presenter by instance()
    private val presenterPermission: PermissionContract.Presenter by instance()
    private val dialog by lazy { createProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
    }

    override fun onResume() {
        super.onResume()
        presenterPermission.bind(this)
        presenterPermission.requestPermissions(this, PERMISSIONS_REQUEST_CODE)

        presenterAuthentication.attach(this)
        presenterAuthentication.checkAuthentication()
        presenterAuthentication.checkBluetoothDevice()

    }

    override fun onPause() {
        super.onPause()
        presenterAuthentication.detach()
        presenterPermission.unbind()
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
                    .putExtra("estorno",false)
            )
        }

        binding.autenticacaoButton.setOnClickListener {
            presenterAuthentication.requestAuthentication()
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
        Toast.makeText(this, R.string.authentication_success, Toast.LENGTH_SHORT).show()
    }

    override fun showMissingAuthenticationView() {
        Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}