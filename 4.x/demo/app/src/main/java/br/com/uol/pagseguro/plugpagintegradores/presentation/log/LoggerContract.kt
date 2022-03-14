package br.com.uol.pagseguro.plugpagintegradores.presentation.log

interface LoggerContract {
    fun information(source: Any, msg: String, throwable: Throwable? = null)
    fun information(msg: String, throwable: Throwable? = null)

    fun debug(source: Any, msg: String, throwable: Throwable? = null)
    fun debug(msg: String, throwable: Throwable? = null)

    fun error(source: Any, msg: String, throwable: Throwable? = null)
    fun error(msg: String, throwable: Throwable? = null)
}