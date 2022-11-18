package br.com.uol.pagseguro.plugpagintegradores.presentation.auth

import br.com.uol.pagseguro.plugpag.PlugPag
import br.com.uol.pagseguro.plugpag.PlugPagAuthenticationListener
import io.reactivex.rxjava3.core.Single

class AuthenticationUseCase(
    private val plugpag: PlugPag
) : AuthenticationUseCaseContract {

    companion object {
        private const val SUCCESS_CODE = "0000"
    }

    override fun checkAuthentication(): Single<Boolean> {
        return Single.create {
            try {
                it.onSuccess(plugpag.isAuthenticated)
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    override fun requestAuthentication(authenticationListener: PlugPagAuthenticationListener) {
        plugpag.requestAuthentication(authenticationListener)
    }

    override fun invalidateAuthentication() {
        plugpag.invalidateAuthentication()
    }

    override fun isSerialDevice() = !plugpag.serialDevices.isNullOrEmpty()

    override fun sleep() = plugpag.sleepPinpad()

    override fun wakeup() = plugpag.wakeupPinpad()

}