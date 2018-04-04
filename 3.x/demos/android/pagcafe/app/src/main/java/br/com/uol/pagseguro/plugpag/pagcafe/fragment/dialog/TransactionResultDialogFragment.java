package br.com.uol.pagseguro.plugpag.pagcafe.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;

import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.InvalidContextTypeException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;


public class TransactionResultDialogFragment
        extends DialogFragment {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Toolbar mToolbar = null;
    private TextView mTxtMessage = null;
    private TableLayout mTblResult = null;

    private OnFragmentInteractionListener mListener = null;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_transaction_result_dialog, container, false);

        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setupViews(view);
        this.parseArgs(this.getArguments());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new InvalidContextTypeException("Context isn't of type OnFragmentInteractionListener");
        }

        this.mListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    // ---------------------------------------------------------------------------------------------
    // Setup Views
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups Views.
     *
     * @param view Root View.
     */
    private void setupViews(@NonNull View view) {
        this.mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.mTxtMessage = (TextView) view.findViewById(R.id.txt_message);
        this.mTblResult = (TableLayout) view.findViewById(R.id.tbl_result);
    }

    // ---------------------------------------------------------------------------------------------
    // Arguments parse
    // ---------------------------------------------------------------------------------------------

    /**
     * Parses the arguments passed to the DialogFragment.
     *
     * @param args Arguments to be parsed.
     */
    private void parseArgs(@Nullable Bundle args) {
        if (args != null) {
            // Title
            if (args.getString(FragmentInteraction.KEY_DIALOG_TITLE) == null) {
                this.mToolbar.setVisibility(View.GONE);
            } else {
                this.mToolbar.setVisibility(View.VISIBLE);
                this.mToolbar.setTitle(args.getString(FragmentInteraction.KEY_DIALOG_TITLE));
            }

            // Type
            if (args.containsKey(FragmentInteraction.KEY_DIALOG_TYPE) &&
                    FragmentInteraction.DIALOG_TYPE_ERROR == args.getInt(FragmentInteraction.KEY_DIALOG_TYPE)) {
                this.mToolbar.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimaryRed));
            }

            // Dismissable
            if (!args.getBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, true)) {
                this.getDialog().setCancelable(false);
                this.getDialog().setCanceledOnTouchOutside(false);
            }

            // Message
            if (args.containsKey(FragmentInteraction.KEY_DIALOG_MESSAGE) &&
                    !TextUtils.isEmpty(args.getString(FragmentInteraction.KEY_DIALOG_MESSAGE))) {
                this.mTxtMessage.setText(args.getString(FragmentInteraction.KEY_DIALOG_MESSAGE));
            } else {
                this.mTxtMessage.setVisibility(View.GONE);
            }

            // Result
            if (args.containsKey(FragmentInteraction.KEY_DIALOG_TRANSACTION_RESULT)) {
                this.fillTransactionResult((PlugPagTransactionResult) args.getSerializable(FragmentInteraction.KEY_DIALOG_TRANSACTION_RESULT));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Transaction result display
    // ---------------------------------------------------------------------------------------------

    /**
     * Fills the transaction result table.
     *
     * @param result Transaction result used to fill the table.
     */
    private void fillTransactionResult(PlugPagTransactionResult result) {
        LayoutInflater inflater = null;
        String displayValue = null;

        inflater = LayoutInflater.from(this.getContext());

        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_result), result.getResult());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_transaction_code), result.getTransactionCode());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_date), result.getDate());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_time), result.getTime());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_host_nsu), result.getHostNsu());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_card_brand), result.getCardBrand());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_bin), result.getBin());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_holder), result.getHolder());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_user_reference), result.getUserReference());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_serial_number), result.getTerminalSerialNumber());
        this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_transaction_id), result.getTransactionId());

        try {
            if (!TextUtils.isEmpty(result.getAmount())) {
                displayValue = String.format("R$ %.02f", new BigDecimal(result.getAmount()).divide(new BigDecimal(100.0)).floatValue());
                this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_amount), displayValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(result.getAvailableBalance())) {
                displayValue = String.format("R$ %.02f", new BigDecimal(result.getAvailableBalance()).divide(new BigDecimal(100.0)).floatValue());
                this.addRowIfValueExists(inflater, this.mTblResult, this.getString(R.string.transaction_result_label_available_balance), displayValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inflaters a new TableRow and fills its Views' content.
     *
     * @param inflater LayoutInflater used to inflate the TableRow layout resource.
     * @param parent   TableRow's parent.
     * @param label    Label to be displayed.
     * @param value    Value to be displayed.
     * @return TableRow ready to be added to its parent View.
     */
    private TableRow inflateAndFillRow(@NonNull LayoutInflater inflater,
                                       @NonNull ViewGroup parent,
                                       @NonNull String label,
                                       @NonNull String value) {
        TableRow row = null;

        row = (TableRow) inflater.inflate(R.layout.row_transaction_result, parent, false);
        ((TextView) row.findViewById(R.id.txt_label)).setText(label);
        ((TextView) row.findViewById(R.id.txt_value)).setText(value);

        return row;
    }

    /**
     * Adds a new TableRow to a TableLayout if the {@code value} is not null nor empty.
     *
     * @param inflater LayoutInflater used to inflate the TableRow layout resource.
     * @param parent   TableRow's parent.
     * @param label    Label to be displayed.
     * @param value    Value to be displayed.
     */
    private void addRowIfValueExists(@NonNull LayoutInflater inflater,
                                     @NonNull TableLayout parent,
                                     @NonNull String label,
                                     @NonNull String value) {
        TableRow row = null;

        if (!TextUtils.isEmpty(value)) {
            row = this.inflateAndFillRow(inflater, parent, label, value);
            parent.addView(row);
        }
    }

    /**
     * Adds a new TableRow to a TableLayout if the {@code value} is not {@link PlugPag#RET_OK}.
     *
     * @param inflater LayoutInflater used to inflate the TableRow layout resource.
     * @param parent   TableRow's parent.
     * @param label    Label to be displayed.
     * @param value    Value to be displayed.
     */
    private void addRowIfValueExists(@NonNull LayoutInflater inflater,
                                     @NonNull TableLayout parent,
                                     @NonNull String label,
                                     @NonNull int value) {
        TableRow row = null;

        row = this.inflateAndFillRow(inflater, parent, label, String.valueOf(value));
        parent.addView(row);
    }

}
