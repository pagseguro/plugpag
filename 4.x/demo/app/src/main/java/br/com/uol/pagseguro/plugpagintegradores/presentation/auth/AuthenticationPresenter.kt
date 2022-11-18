package br.com.uol.pagseguro.plugpagintegradores.presentation.auth

import br.com.uol.pagseguro.plugpag.PlugPagAuthenticationListener
import br.com.uol.pagseguro.plugpagintegradores.data.local.DataStorageContract
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AuthenticationPresenter(
    private val authenticationUseCase: AuthenticationUseCaseContract,
    private val storage: DataStorageContract
) : AuthenticationContract.Presenter {

    var view: AuthenticationContract.View? = null

    private val disposables = CompositeDisposable()

    override fun attach(view: AuthenticationContract.View) {
        this.view = view
    }

    override fun detach() {
        disposables.clear()
        view = null
    }

    override fun checkAuthentication() {
        disposables.add(authenticationUseCase.checkAuthentication()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showLoadingAnimation() }
            .doOnSuccess {
                if (it) {
                    view?.showAuthenticatedSession()
                } else {
                    view?.showMissingAuthenticationView()
                }

                view?.hideLoadingAnimation()
            }
            .doOnError {
                view?.hideLoadingAnimation()
            }
            .subscribe())
    }

    override fun checkBluetoothDevice() {
        disposables.add(Single
            .create<String> {
                try {
                    it.onSuccess(storage.getSelectedBluetoothDevice())
                } catch (e: Exception) {
                    it.tryOnError(e)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { deviceName ->
                    view?.apply {
                        if (deviceName.isNotEmpty()) {
                            showToast(deviceName)
                        }
                    }
                },

                onError = { error ->
                    view?.showToast(error.message.toString())
                }
            ))
    }

    override fun requestAuthentication() {
            authenticationUseCase.requestAuthentication(object : PlugPagAuthenticationListener {
                override fun onSuccess() {
                    checkAuthentication()
                }

                override fun onError() {
                    checkAuthentication()
                }
            })
    }

    override fun invalidateAuthentication() {
        authenticationUseCase.invalidateAuthentication()
        checkAuthentication()
    }

    override fun isSerialDevice() = authenticationUseCase.isSerialDevice()

    override fun sleep() = authenticationUseCase.sleep()

    override fun wakeup() = authenticationUseCase.wakeup()

}