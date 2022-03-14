package br.com.uol.pagseguro.plugpagintegradores.presentation.log

import android.util.Log

class LogcatLogger : LoggerContract {

    companion object {
        const val TAG = "PlugPag Integradores"
    }

    override fun information(source: Any, msg: String, throwable: Throwable?) {
        Log.i(TAG, "[${source::class.java}] $msg", throwable)
    }

    override fun information(msg: String, throwable: Throwable?) {
        Log.i(TAG, msg, throwable)
    }

    override fun debug(source: Any, msg: String, throwable: Throwable?) {
        Log.d(TAG, "[${source::class.java}] $msg", throwable)
    }

    override fun debug(msg: String, throwable: Throwable?) {
        Log.d(TAG, msg, throwable)
    }

    override fun error(source: Any, msg: String, throwable: Throwable?) {
        Log.e(TAG, "[${source::class.java}] $msg", throwable)
    }

    override fun error(msg: String, throwable: Throwable?) {
        Log.e(TAG, msg, throwable)
    }
}