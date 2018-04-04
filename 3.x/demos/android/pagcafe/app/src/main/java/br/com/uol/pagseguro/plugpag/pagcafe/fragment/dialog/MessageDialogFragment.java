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
import android.widget.TextView;

import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.InvalidContextTypeException;


public class MessageDialogFragment
        extends DialogFragment {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Toolbar mToolbar = null;
    private TextView mTxtMessage = null;

    private OnFragmentInteractionListener mListener = null;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_message_dialog, container, false);

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
            if (TextUtils.isEmpty(args.getString(FragmentInteraction.KEY_DIALOG_TITLE))) {
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
                this.mTxtMessage.setVisibility(View.VISIBLE);
                this.mTxtMessage.setText(args.getString(FragmentInteraction.KEY_DIALOG_MESSAGE));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Update message
    // ---------------------------------------------------------------------------------------------

    /**
     * Updates the message being displayed.
     *
     * @param message Message to be displayed.
     */
    public void updateMessage(@NonNull String message) {
        this.mTxtMessage.setText(message);
    }

}
