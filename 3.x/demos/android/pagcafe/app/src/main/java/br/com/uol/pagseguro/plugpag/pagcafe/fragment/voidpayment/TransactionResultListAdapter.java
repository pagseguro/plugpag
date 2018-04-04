package br.com.uol.pagseguro.plugpag.pagcafe.fragment.voidpayment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public class TransactionResultListAdapter extends ArrayAdapter<PlugPagTransactionResult> {

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    public TransactionResultListAdapter(@NonNull Context context,
                                        int resource,
                                        @NonNull List<PlugPagTransactionResult> data) {
        super(context, resource, data);
    }

    // ---------------------------------------------------------------------------------------------
    // List drawing
    // ---------------------------------------------------------------------------------------------

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        PlugPagTransactionResult result = null;

        view = convertView;
        result = this.getItem(position);

        if (view == null) {
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.row_transaction_result_query, parent, false);
        }

        ((TextView) view).setText(String.format("R$ %.02f (%s) - %s",
                new BigDecimal(result.getAmount()).divide(new BigDecimal(100)).floatValue(),
                result.getCardBrand(),
                result.getDate()));
        view.setTag(result);

        return view;
    }

}
