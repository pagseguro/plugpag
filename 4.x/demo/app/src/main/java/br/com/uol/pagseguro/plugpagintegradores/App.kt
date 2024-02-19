package br.com.uol.pagseguro.plugpagintegradores

import android.Manifest
import android.app.Application
import android.content.Context
import br.com.uol.pagseguro.plugpag.PlugPag
import br.com.uol.pagseguro.plugpag.PlugPagAppIdentification
import br.com.uol.pagseguro.plugpagintegradores.data.local.DataStorageContract
import br.com.uol.pagseguro.plugpagintegradores.data.local.SharedPrefDataStorage
import br.com.uol.pagseguro.plugpagintegradores.presentation.auth.AuthenticationContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.auth.AuthenticationPresenter
import br.com.uol.pagseguro.plugpagintegradores.presentation.auth.AuthenticationUseCase
import br.com.uol.pagseguro.plugpagintegradores.presentation.auth.AuthenticationUseCaseContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth.BluetoothContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth.BluetoothPresenter
import br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth.BluetoothUseCase
import br.com.uol.pagseguro.plugpagintegradores.presentation.bluetooth.BluetoothUseCaseContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.log.LogcatLogger
import br.com.uol.pagseguro.plugpagintegradores.presentation.log.LoggerContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.payment.PaymentContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.payment.PaymentPresenter
import br.com.uol.pagseguro.plugpagintegradores.presentation.permission.AppPermissionsUseCase
import br.com.uol.pagseguro.plugpagintegradores.presentation.permission.AppPermissionsUseCaseContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.permission.PermissionContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.permission.PermissionPresenter
import br.com.uol.pagseguro.plugpagintegradores.presentation.transaction.ListTransactionContract
import br.com.uol.pagseguro.plugpagintegradores.presentation.transaction.ListTransactionPresenter
import org.kodein.di.KodeinAware
import org.kodein.di.conf.ConfigurableKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class App : Application(), KodeinAware {

    override val kodein = ConfigurableKodein(mutable = true)

    init {
        kodein.addConfig {

            bind<Context>() with singleton { this@App }
            bind<PlugPagAppIdentification>() with singleton {
                PlugPagAppIdentification(
                    "PlugPagIntegradores",
                    "0.0.0"
                )
            }
            bind<PlugPag>() with singleton { PlugPag(instance()) }

            bind<LoggerContract>() with singleton { LogcatLogger() }

            bind<DataStorageContract>() with provider {
                SharedPrefDataStorage(instance(), instance())
            }

            bind<BluetoothContract.Presenter>() with singleton {
                BluetoothPresenter(
                    instance(),
                    instance()
                )
            }
            bind<BluetoothUseCaseContract>() with provider { BluetoothUseCase() }
            bind<PaymentContract.Presenter>() with singleton {
                PaymentPresenter(
                    instance(),
                    instance(),
                    instance()
                )
            }

            bind<AuthenticationUseCaseContract>() with provider {
                AuthenticationUseCase(instance())
            }
            bind<AuthenticationContract.Presenter>() with singleton {
                AuthenticationPresenter(
                    instance(),
                    instance()
                )
            }

            bind<ListTransactionContract.Presenter>() with singleton {
                ListTransactionPresenter(
                    instance(),
                    instance()
                )
            }

            bind<AppPermissionsUseCaseContract>() with provider {
                AppPermissionsUseCase(
                    instance(),
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            bind<PermissionContract.Presenter>() with singleton {
                PermissionPresenter(
                    instance()
                )
            }
        }
    }
}
