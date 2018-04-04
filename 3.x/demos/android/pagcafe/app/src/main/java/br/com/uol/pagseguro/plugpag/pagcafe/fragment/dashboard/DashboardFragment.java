package br.com.uol.pagseguro.plugpag.pagcafe.fragment.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.InvalidContextTypeException;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.MissingFragmentInteractionInfoException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.coffeepayment.CoffeePaymentFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.voidpayment.VoidPaymentFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;


public class DashboardFragment
        extends Fragment
        implements View.OnClickListener {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    public static final String TAG = "dashboard_fragment";

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Button mBtnSimplePayment = null;
    private Button mBtnCoffeePayment = null;
    private Button mBtnVoidPayment = null;
    private Button mBtnQueryLastSuccessfulTransaction = null;

    private OnFragmentInteractionListener mInteractionListener = null;

    // ---------------------------------------------------------------------------------------------
    // Fragment creation
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new DashboardFragment instance.
     *
     * @return New DashboardFragment instance.
     */
    public static final DashboardFragment newInstance() {
        DashboardFragment fragment = null;

        fragment = new DashboardFragment();

        return fragment;
    }

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = null;

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setupViews(view);
    }

    @Override
    public void onAttach(Context context) {
        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new InvalidContextTypeException("Context reference must be of OnFragmentInteractionListener type");
        }

        super.onAttach(context);
        this.mInteractionListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mInteractionListener = null;
    }

    // ---------------------------------------------------------------------------------------------
    // View setup
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups Views.
     *
     * @param view Root view.
     */
    private void setupViews(View view) {
        this.mBtnSimplePayment = (Button) view.findViewById(R.id.btnSimplePayment);
        this.mBtnCoffeePayment = (Button) view.findViewById(R.id.btnCoffeePayment);
        this.mBtnVoidPayment = (Button) view.findViewById(R.id.btnVoidPayment);
        this.mBtnQueryLastSuccessfulTransaction = (Button) view.findViewById(R.id.btnQueryLastSuccessfulTransaction);

        this.mBtnSimplePayment.setOnClickListener(this);
        this.mBtnCoffeePayment.setOnClickListener(this);
        this.mBtnVoidPayment.setOnClickListener(this);
        this.mBtnQueryLastSuccessfulTransaction.setOnClickListener(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Buttons event handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        OnFragmentInteractionListener.FragmentInteractionInfo interactionInfo = null;
        Bundle args = null;
        PlugPagTransactionResult res = null;

        switch (v.getId()) {
            case R.id.btnSimplePayment:
//                args = this.createShowFragmentInteractionBundle(SimplePaymentFragment.class, "SimplePayment");
                break;

            case R.id.btnCoffeePayment:
                args = this.createShowFragmentInteractionBundle(CoffeePaymentFragment.class, "CoffeePayment");
                break;

            case R.id.btnVoidPayment:
                if (Bluetooth.getSelectedBluetoothDevice() == null) {
                    args = this.createShowMessageInteractionBundle(
                            this.getString(R.string.title_error),
                            this.getString(R.string.msg_no_bluetooth_device_selected_warning),
                            FragmentInteraction.DIALOG_TYPE_ERROR);
                } else {
//                    if (BluetoothDeviceType.isTerminal(Bluetooth.getSelectedBluetoothDevice().getName())) {
//                        PlugPagDevice device = new PlugPagDevice();
//                        device.setIdentification(Bluetooth.getSelectedBluetoothDevice().getName());
//                        new VoidPaymentTask(this, device).execute();
//                    } else {
//                        args = this.createShowFragmentInteractionBundle(VoidPaymentFragment.class, "VoidPayment");
//                    }
                    args = this.createShowFragmentInteractionBundle(VoidPaymentFragment.class, "VoidPayment");
                }

                break;

            case R.id.btnQueryLastSuccessfulTransaction:
//                args = this.createShowFragmentInteractionBundle(QueryLastSuccessfulTransactionFragment.class, "QueryLastSuccessfulTransaction");
                args = this.createShowMessageInteractionBundle("Consulta", "Botão de consulta clicado", FragmentInteraction.DIALOG_TYPE_ERROR);
                break;
        }

        if (this.mInteractionListener != null && args != null) {
            interactionInfo = new OnFragmentInteractionListener.FragmentInteractionInfo(args);
            this.mInteractionListener.onInteract(interactionInfo);
        }
    }

    /**
     * Creates a Bundle for a Fragment interaction to show a new Fragment.
     *
     * @param cls New Fragment class.
     * @param tag New Fragment tag.
     * @return Bundle used to show a new Fragment on a Fragment interaction.
     */
    private Bundle createShowFragmentInteractionBundle(@NonNull Class<? extends Fragment> cls,
                                                       @Nullable String tag) {
        Bundle bundle = null;

        bundle = new Bundle();
        bundle.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_FRAGMENT);
        bundle.putSerializable(FragmentInteraction.KEY_FRAGMENT_CLASS, cls);
        bundle.putString(FragmentInteraction.KEY_FRAGMENT_TAG, tag);

        return bundle;
    }

    /**
     * Creates a Bundle for a Fragment interaction to show a Dialog.
     *
     * @param title   Dialog title.
     * @param message Message to be displayed into the Dialog.
     * @param type    Dialog type.
     * @return Bundle used to show a Dialog on a Fragment interaction.
     */
    private Bundle createShowMessageInteractionBundle(@Nullable String title,
                                                      @NonNull String message,
                                                      int type) {
        Bundle bundle = null;

        if (TextUtils.isEmpty(message)) {
            throw new MissingFragmentInteractionInfoException("Missing DialogFragment message");
        }

        bundle = new Bundle();
        bundle.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_DIALOG);
        bundle.putInt(FragmentInteraction.KEY_DIALOG_TYPE, type);
        bundle.putString(FragmentInteraction.KEY_DIALOG_TITLE, title);
        bundle.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);

        return bundle;
    }
}
