package br.com.uol.pagseguro.plugpagintegradores.presentation.permission

import android.app.Activity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class PermissionPresenter(
    private val permissionsUseCase: AppPermissionsUseCaseContract
) : PermissionContract.Presenter {

    private var view: PermissionContract.View? = null
    private val disposables = CompositeDisposable()

    override fun bind(view: PermissionContract.View) {
        this.view = view
    }

    override fun unbind() {
        disposables.clear()
        view = null
    }

    override fun requestPermissions(activity: Activity, requestCode: Int) {
        disposables.add(Completable
            .create {
                val missingPermissions = permissionsUseCase.getMissingPermissions()

                if (missingPermissions.isNotEmpty()) {
                    permissionsUseCase.requestPermissions(
                        activity,
                        missingPermissions,
                        requestCode
                    )
                }

                it.onComplete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

}