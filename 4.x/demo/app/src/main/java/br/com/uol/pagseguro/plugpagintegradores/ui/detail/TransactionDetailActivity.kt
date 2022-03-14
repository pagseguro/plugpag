package br.com.uol.pagseguro.plugpagintegradores.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.uol.pagseguro.plugpagintegradores.databinding.ActivityTransactionDetailBinding

class TransactionDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityTransactionDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}