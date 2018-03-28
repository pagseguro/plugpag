package br.uol.pagseguro.client.pagcafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.uol.pagseguro.client.pagcafe.R;
import br.uol.pagseguro.client.pagcafe.fragment.ResultFragment;
import br.uol.pagseguro.client.pagcafe.model.Transaction;
import br.uol.pagseguro.client.pagcafe.model.Type_Transaction;

/**
 * <p>{@link AppCompatActivity} para exibir um menu com as operações disponíveis na aplicação.</p>
 *
 * <p>Essa {@link AppCompatActivity} é o ponto de entrada da aplicação.</p>
 *
 * <p>As operações disponíveis são:</p>
 *
 * <ul>
 *     <li><em>Fazer uma venda</em>: Inicia a {@link AppCompatActivity} de venda e pagamento.</li>
 *     <li><em>Fazer estorno</em>: Inicia a transação de estorno de pagamentos.</li>
 *     <li><em>Consultar última venda</em>: Inicia a transação para exibir o último pagamento
 *     efetuado.</li>
 * </ul>
 *
 * <p>{@link #onCreate}: Cria referências para as Views e define seus event listeners.</p>
 *
 * <p>{@link #onClick}: Decide que operações devem ser executadas baseando-se no ID da View que gerou
 * o evento.</p>
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    // ---------------------------------------------------------------------------------------------
    // View references
    // ---------------------------------------------------------------------------------------------

    private Button mBtnSale = null;
    private Button mBtnSaleReversal = null;
    private Button mBtnQueryLastSale = null;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Setup Views
        this.setupViewsReferences();
        this.setupEventListeners();
    }

    // ---------------------------------------------------------------------------------------------
    // Views initialization
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups View references.
     */
    private void setupViewsReferences() {
        this.mBtnSale = (Button) this.findViewById(R.id.btnSale);
        this.mBtnSaleReversal = (Button) this.findViewById(R.id.btnCancelSale);
        this.mBtnQueryLastSale = (Button) this.findViewById(R.id.btnQueryLastSale);
    }

    /**
     * Setups event listeners for the Activity's Views.
     */
    private void setupEventListeners() {
        this.mBtnSale.setOnClickListener(this);
        this.mBtnSaleReversal.setOnClickListener(this);
        this.mBtnQueryLastSale.setOnClickListener(this);
    }

    // ---------------------------------------------------------------------------------------------
    // View click events
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        int id = 0;
        Intent intent = null;

        id = v.getId();

        switch (id) {
            case R.id.btnSale:
                intent = new Intent(this, CoffeeSelectionActivity.class);
                break;

            case R.id.btnCancelSale:
                ResultFragment
                        .newInstance(new Transaction(Type_Transaction.CANCEL_TRANSACTION))
                        .show(this.getSupportFragmentManager().beginTransaction(), "");
                break;

            case R.id.btnQueryLastSale:
                ResultFragment
                        .newInstance(null)
                        .show(this.getSupportFragmentManager().beginTransaction(), "");
                break;
        }

        if (intent != null) {
            // Start the coffee selection and payment Activity
            this.startActivity(intent);
        }
    }

}