package br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.widget.Toast
import br.com.uol.pagseguro.plugpagintegradores.data.local.DataStorageContract
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class BluetoothPresenter(
    private val bluetoothUseCase: BluetoothUseCaseContract,
    private val storage: DataStorageContract
) : BluetoothContract.Presenter {

    private var view: BluetoothContract.View? = null

    private val disposables = CompositeDisposable()

    override fun attach(view: BluetoothContract.View) {
        this.view = view
    }

    override fun detach() {
        disposables.clear()
        view = null
    }

    override fun loadPairedDevices() {
        disposables.add(Single
            .create<List<BluetoothDevice>> {
                it.onSuccess(bluetoothUseCase.getPairedDevices())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    view?.showPairedDevices(it)
                },

                onError = {
                    view?.showErrorLoadingPairedDevices()
                }
            ))
    }

    override fun saveSelectedBluetoothDevice(device: BluetoothDevice) {
        disposables.add(Single
            .create<Boolean> {
                try {
                    it.onSuccess(storage.saveSelectedBluetoothDevice(device))
                } catch (e: Exception) {
                    it.tryOnError(e)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { it.message?.let { it1 -> Log.d("Error save ->", it1) } }
            .subscribe())
    }

    override fun getSelectedBluetoothDeviceName(activity: Activity): String {
        val device = storage.getSelectedBluetoothDevice()
        Toast
            .makeText(activity, device, Toast.LENGTH_SHORT)
            .show()

        return device
    }

}