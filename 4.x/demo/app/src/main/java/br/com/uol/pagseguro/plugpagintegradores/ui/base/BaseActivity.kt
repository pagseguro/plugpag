package br.com.uol.pagseguro.plugpagintegradores.ui.base

import androidx.appcompat.app.AppCompatActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

abstract class BaseActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by closestKodein()
}