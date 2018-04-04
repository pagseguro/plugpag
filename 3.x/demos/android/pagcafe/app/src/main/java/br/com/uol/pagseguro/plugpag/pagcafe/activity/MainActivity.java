package br.com.uol.pagseguro.plugpag.pagcafe.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uol.pagseguro.plugpag.pagcafe.OnAbortListener;
import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.BluetoothDeviceType;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.BluetoothStateChangeBroadcastReceiver;
import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.OnBluetoothStateChanged;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.MissingFragmentInteractionInfoException;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.bluetooth.BluetoothListFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.coffeepayment.CoffeePaymentFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.dialog.MessageDialogFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.dialog.ProgressDialogFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.dialog.TransactionResultDialogFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragment.voidpayment.VoidPaymentFragment;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.network.NetworkChangeBroadcastReceiver;
import br.com.uol.pagseguro.plugpag.pagcafe.network.OnNetworkChanged;
import br.com.uol.pagseguro.plugpag.pagcafe.plugpag.PlugPagManager;
import br.com.uol.pagseguro.plugpag.pagcafe.task.QueryLastApprovedTransactionTask;
import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public final class MainActivity
        extends AppCompatActivity
        implements OnFragmentInteractionListener, Bluetooth.BluetoothObservable, OnNetworkChanged,
        QueryLastApprovedTransactionTask.OnQueryLastApprovedTransactionListener,
        OnAbortListener, OnBluetoothStateChanged {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    private static final long QUIT_THRESHOLD = 1000;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Toolbar mToolbar = null;
    private TextView mTxtSelectedBluetoothDevice = null;
    private TextView mTxtPlugPagVersion = null;
    private TextView mTxtNetworkInfo = null;

    private List<BroadcastReceiver> mBroadcastReceivers = null;
    private long mBackPressedTime = -1L;
    private MessageDialogFragment mDialogFragment = null;
    private ProgressDialogFragment mProgressDialogFragment = null;
    private TransactionResultDialogFragment mTransactionResultDialogFragment = null;

    private boolean mClearOptionsMenu = false;
    private boolean mIsResumed = false;
    private FragmentInteractionInfo mPendingInteraction = null;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setupViews();
        Bluetooth.register(this);
        PlugPagManager.initialize(this.getApplicationContext());

        this.showFragment(new CoffeePaymentFragment(), CoffeePaymentFragment.TAG, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.mIsResumed = true;

        this.requestAllPermissions();
        this.fillFooter();
        this.registerBroadcastReceivers();

        if (Bluetooth.getSelectedBluetoothDevice() == null) {
            this.showBluetoothDeviceList();
        }

        if (this.mPendingInteraction != null) {
            this.onInteract(this.mPendingInteraction);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mIsResumed = false;
        this.unregisterBroadcastReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mIsResumed = false;
        Bluetooth.unregister(this);
        PlugPagManager.release();
    }

    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 1) {
            // Pop current Fragment from back stack
            this.mBackPressedTime = -1L;
            this.mClearOptionsMenu = false;
            this.getSupportFragmentManager().popBackStack();
            this.invalidateOptionsMenu();
        } else if (System.currentTimeMillis() - this.mBackPressedTime <= MainActivity.QUIT_THRESHOLD) {
            // Close application (pop last Fragment from back stack then call Back button again)
            this.getSupportFragmentManager().popBackStack();
            super.onBackPressed();
        } else {
            // Show instruction on how to quit the application
            Toast.makeText(this, R.string.msg_back_pressed_warning, Toast.LENGTH_SHORT).show();
            this.mBackPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PlugPag.RET_OK) {
            FragmentInteraction.showMessage(
                    this,
                    FragmentInteraction.DIALOG_TYPE_NORMAL,
                    this.getString(R.string.title_success),
                    this.getString(R.string.msg_authentication_successful));
        } else {
            FragmentInteraction.showMessage(
                    this,
                    FragmentInteraction.DIALOG_TYPE_ERROR,
                    this.getString(R.string.title_error),
                    this.getString(R.string.msg_authentication_failed));
        }
    }
// ---------------------------------------------------------------------------------------------
    // Menu handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.toolbar, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem voidItem = null;
        MenuItem queryTransactionItem = null;
        MenuItem authItem = null;
        MenuItem bluetoothListItem = null;
        boolean isBluetoothDeviceSelected = false;

        super.onPrepareOptionsMenu(menu);
        isBluetoothDeviceSelected = Bluetooth.getSelectedBluetoothDevice() != null;

        voidItem = menu.findItem(R.id.toolbar_action_void_payment);
        queryTransactionItem = menu.findItem(R.id.toolbar_action_query_last_approved_transaction);
        authItem = menu.findItem(R.id.toolbar_action_authenticate);
        bluetoothListItem = menu.findItem(R.id.toolbar_action_bluetooth_list);

        if (this.mClearOptionsMenu) {
            voidItem.setVisible(false);
            queryTransactionItem.setVisible(false);
            authItem.setVisible(false);
            bluetoothListItem.setVisible(false);
        } else {
            // Prepare void payment transaction icon
            voidItem.setVisible(isBluetoothDeviceSelected);

            // Prepare query transaction icon
            queryTransactionItem.setVisible(
                    BluetoothDeviceType.isTerminal(Bluetooth.getSelectedBluetoothDevice()));

            // Prepare authentication icon
            authItem.setVisible(isBluetoothDeviceSelected &&
                    !BluetoothDeviceType.isTerminal(Bluetooth.getSelectedBluetoothDevice()));

            // Prepare bluetooth list icon
            bluetoothListItem.setVisible(true);
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------
    // Fragment handling
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows a Fragment.
     *
     * @param fragment       Fragment to be displayed.
     * @param tag            Fragment tag.
     * @param addToBackStack If the Fragment must be added to the back stack.
     */
    private void showFragment(@NonNull Fragment fragment, @NonNull String tag, boolean addToBackStack) {
        FragmentTransaction ft = null;

        ft = this.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, R.anim.slide_out_left, android.R.anim.slide_in_left, R.anim.slide_out_left);

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        ft.replace(R.id.main_container, fragment, tag);
        ft.commit();
    }

    // ---------------------------------------------------------------------------------------------
    // BroadcastReceivers
    // ---------------------------------------------------------------------------------------------

    /**
     * Register BroadcastReceivers.
     */
    private void registerBroadcastReceivers() {
        BroadcastReceiver receiver = null;

        // Unregister old broadcast receivers
        this.unregisterBroadcastReceivers();

        // Keep receiver reference.
        if (this.mBroadcastReceivers == null) {
            this.mBroadcastReceivers = new ArrayList<>();
        }

        this.mBroadcastReceivers.add(this.registerNetworkChangeReceiver());
        this.mBroadcastReceivers.add(this.registerBluetoothStateChangeReceiver());
    }

    /**
     * Creates and registers a network change broadcast receiver.
     *
     * @return BroadcastReceiver created and register to listen to network changes.
     */
    private BroadcastReceiver registerNetworkChangeReceiver() {
        BroadcastReceiver receiver = null;
        IntentFilter filter = null;

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeBroadcastReceiver(this);
        this.registerReceiver(receiver, filter);

        return receiver;
    }

    /**
     * Creates and registers a bluetooth state broadcast receiver.
     *
     * @return BroadcastReceiver created and register to listen to bluetooth state changes.
     */
    private BroadcastReceiver registerBluetoothStateChangeReceiver() {
        BroadcastReceiver receiver = null;
        IntentFilter filter = null;

        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new BluetoothStateChangeBroadcastReceiver(this);
        this.registerReceiver(receiver, filter);

        return receiver;
    }

    /**
     * Unregister all BroadcastReceivers.
     */
    private void unregisterBroadcastReceivers() {
        if (this.mBroadcastReceivers != null) {
            for (BroadcastReceiver receiver : this.mBroadcastReceivers) {
                try {
                    this.unregisterReceiver(receiver);
                } catch (Exception e) {
                    // Do nothing, because the receiver just isn't registered
                }
            }

            this.mBroadcastReceivers.clear();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Menu preparation
    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean consumed = false;
        int result = PlugPag.RET_OK;

        this.mClearOptionsMenu = false;

        switch (item.getItemId()) {
            case R.id.toolbar_action_void_payment:
                this.mClearOptionsMenu = true;
                this.handleVoidPaymentTransactionSelection();
                consumed = true;
                break;

            case R.id.toolbar_action_query_last_approved_transaction:
                this.handleQueryLastApprovedTransactionSelection();
                consumed = true;
                break;

            case R.id.toolbar_action_authenticate:
                result = PlugPagManager.getInstance().getPlugPag().requestAuthentication(this);
                consumed = true;

                if (result == PlugPag.ERROR_REQUIREMENTS_MISSING_PERMISSIONS) {
                    FragmentInteraction.showErrorMessage(
                            this,
                            this.getString(R.string.title_error),
                            this.getString(R.string.msg_missing_permissions));
                }

                break;

            case R.id.toolbar_action_bluetooth_list:
                this.showBluetoothDeviceList();
                consumed = true;
                break;

            default:
                consumed = super.onOptionsItemSelected(item);
        }

        this.invalidateOptionsMenu();

        return consumed;
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth device selection
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows the bluetooth device selection list.
     */
    private void showBluetoothDeviceList() {
        Fragment fragment = null;

        fragment = this.prepareBluetoothListFragment();
        ((DialogFragment) fragment).show(this.getSupportFragmentManager(), "bluetooth_devices_list");
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth device selection warning
    // ---------------------------------------------------------------------------------------------

    /**
     * Shows a warning of missing bluetooth device selection.
     */
    private void showBluetoothSelectionMissingWarning() {
        FragmentInteraction.showMessage(this,
                FragmentInteraction.DIALOG_TYPE_ERROR,
                this.getString(R.string.title_error),
                this.getString(R.string.msg_no_bluetooth_device_selected_warning));
    }

    // ---------------------------------------------------------------------------------------------
    // Transaction void preparation
    // ---------------------------------------------------------------------------------------------

    /**
     * Handles the attempt to start a void payment transaction.
     */
    private void handleVoidPaymentTransactionSelection() {
        Bundle args = null;

        if (Bluetooth.getSelectedBluetoothDevice() == null) {
            this.showBluetoothSelectionMissingWarning();
        } else {
            args = this.getVoidTransactionArguments();
            this.onInteract(new FragmentInteractionInfo(args));
        }
    }

    /**
     * Returns the arguments needed to start a void payment transaction.
     *
     * @return Arguments needed to start a void payment transaction.
     */
    private Bundle getVoidTransactionArguments() {
        Bundle args = null;

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_FRAGMENT);
        args.putSerializable(FragmentInteraction.KEY_FRAGMENT_CLASS, VoidPaymentFragment.class);
        args.putString(FragmentInteraction.KEY_FRAGMENT_TAG, VoidPaymentFragment.TAG);

        return args;
    }

    // ---------------------------------------------------------------------------------------------
    // Last approved transaction querying
    // ---------------------------------------------------------------------------------------------

    /**
     * Handles a request to show the last approved transaction's result.
     */
    private void handleQueryLastApprovedTransactionSelection() {
        if (Bluetooth.getSelectedBluetoothDevice() == null) {
            this.showBluetoothSelectionMissingWarning();
        } else {
            if (BluetoothDeviceType.isPinpad(Bluetooth.getSelectedBluetoothDevice())) {
                this.onPostQuery(null);
            } else {
                this.startQueryLastApprovedTransactionTask();
            }
        }
    }

    /**
     * Starts a task to query for the last approved transaction.
     */
    private void startQueryLastApprovedTransactionTask() {
        PlugPagDevice device = null;

        device = new PlugPagDevice(Bluetooth.getSelectedBluetoothDevice().getName());

        new QueryLastApprovedTransactionTask(this, device).execute();
    }

    public static int counter = 0;

    @Override
    public void onPreQuery() {
        FragmentInteraction.showProgressDialog(this,
                this.getString(R.string.title_wait),
                this.getString(R.string.msg_processing));
    }

    @Override
    public void onPostQuery(PlugPagTransactionResult result) {
        if (result != null) {
            FragmentInteraction.showTransactionResult(this,
                    this.getString(R.string.title_success),
                    result.getMessage(),
                    result);
        } else {
            FragmentInteraction.showMessage(this,
                    FragmentInteraction.DIALOG_TYPE_ERROR,
                    this.getString(R.string.title_error),
                    this.getString(R.string.msg_query_last_approved_transaction));
        }
    }

    // ---------------------------------------------------------------------------------------------
    // View and listeners setup
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups the Views.
     */
    private void setupViews() {
        this.mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.mTxtSelectedBluetoothDevice = (TextView) this.findViewById(R.id.txt_selected_bluetooth_device);
        this.mTxtPlugPagVersion = (TextView) this.findViewById(R.id.txt_plugpag_version);
        this.mTxtNetworkInfo = (TextView) this.findViewById(R.id.txt_network_info);

        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    // ---------------------------------------------------------------------------------------------
    // Permissions manipulation
    // ---------------------------------------------------------------------------------------------

    /**
     * Requests all missing permissions.
     */
    private void requestAllPermissions() {
        String[] permissions = null;

        permissions = this.getMissingPermissions();

        if (permissions != null && permissions.length > 0) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    /**
     * Returns a list of all permissions needed but not granted.
     *
     * @return List of permissions needed but not granted.
     */
    private String[] getMissingPermissions() {
        List<String> missingPermissions = null;
        String[] allPermissions = null;

        allPermissions = this.getManifestPermissions();
        missingPermissions = new ArrayList<>();

        if (allPermissions != null && allPermissions.length > 0) {
            for (String permission : allPermissions) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission);
                }
            }
        }

        return missingPermissions.toArray(new String[0]);
    }

    /**
     * Returns an array of permissions requested on the AndroidManifest file.
     *
     * @return Permissions requested on the AndroidManifest file.
     */
    private String[] getManifestPermissions() {
        String[] permissions = null;
        PackageInfo pi = null;

        try {
            pi = this.getPackageManager()
                    .getPackageInfo(this.getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            permissions = pi.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            // Should never get here
            e.printStackTrace();
        }

        return permissions;
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth list Fragment preparation
    // ---------------------------------------------------------------------------------------------

    /**
     * Prepares a bluetooth list Fragment to be displayed.
     *
     * @return Bluetooth list Fragment to be displayed.
     */
    private BluetoothListFragment prepareBluetoothListFragment() {
        return BluetoothListFragment.newInstance();
    }

    // ---------------------------------------------------------------------------------------------
    // Bluetooth changes observation
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onSetBluetoothDevice(@Nullable BluetoothDevice device) {
        String text = null;

        if (device != null) {
            text = String.format("%s (%s)", device.getName(), device.getAddress());
        } else {
            text = "";
        }

        this.mTxtSelectedBluetoothDevice.setText(text);
        this.invalidateOptionsMenu();
    }

    // ---------------------------------------------------------------------------------------------
    // Footer data
    // ---------------------------------------------------------------------------------------------

    /**
     * Fills footer Views.
     */
    private void fillFooter() {
        PlugPag plugpag = null;
        NetworkInfo networkInfo = null;

        plugpag = PlugPagManager.getInstance(null).getPlugPag();
        this.mTxtPlugPagVersion.setText(PlugPag.getLibVersion());

        networkInfo = this.getNetworkInfo();
        this.fillFooterNetworkData(networkInfo);
    }

    /**
     * Fills the footer with network information.
     *
     * @param networkInfo Network information used to fill the footer.
     */
    private void fillFooterNetworkData(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            this.mTxtNetworkInfo.setText(String.format("[%s] %s",
                    networkInfo.getTypeName(), networkInfo.getExtraInfo()));
        } else {
            this.mTxtNetworkInfo.setText("");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Network info
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the active network's information.
     *
     * @return Active network information.
     */
    private NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();

        return ni;
    }

    @Override
    public void onNetworkChanged(NetworkInfo networkInfo) {
        this.fillFooterNetworkData(networkInfo);
    }

    // ---------------------------------------------------------------------------------------------
    // Dialogs dismissal
    // ---------------------------------------------------------------------------------------------

    /**
     * Dismisses all Dialogs.
     */
    private void dismissAllDialogs() {
        this.dismissMessageDialog();
        this.dismissProgressDialog();
        this.dismissTransactionResultDialog();
    }

    /**
     * Dismisses the message Dialog.
     */
    private void dismissMessageDialog() {
        if (this.mDialogFragment != null && this.mDialogFragment.isVisible()) {
            this.mDialogFragment.dismiss();
            this.mDialogFragment = null;
        }
    }

    /**
     * Dismisses the transaction result Dialog.
     */
    private void dismissTransactionResultDialog() {
        if (this.mTransactionResultDialogFragment != null && this.mTransactionResultDialogFragment.isVisible()) {
            this.mTransactionResultDialogFragment.dismiss();
            this.mTransactionResultDialogFragment = null;
        }
    }

    /**
     * Dismisses the progress Dialog.
     */
    private void dismissProgressDialog() {
        if (this.mProgressDialogFragment != null && this.mProgressDialogFragment.isVisible()) {
            this.mProgressDialogFragment.dismiss();
            this.mProgressDialogFragment = null;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Fragments interaction
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onInteract(@NonNull FragmentInteractionInfo interactionInfo) {
        Bundle data = null;

        if (interactionInfo == null) {
            throw new MissingFragmentInteractionInfoException("FragmentInteractionInfo reference cannot be null");
        }

        if (!this.mIsResumed) {
            this.mPendingInteraction = interactionInfo;
        } else {
            this.mPendingInteraction = null;
            data = interactionInfo.getData();

            if (!data.containsKey(FragmentInteraction.KEY_ACTION)) {
                throw new MissingFragmentInteractionInfoException("No action given");
            }

            if (FragmentInteraction.ACTION_SHOW_FRAGMENT.equals(data.getString(FragmentInteraction.KEY_ACTION))) {
                // Change content Fragment
                this.handleShowFragmentInteraction(data);
            } else if (FragmentInteraction.ACTION_SHOW_DIALOG.equals(data.getString(FragmentInteraction.KEY_ACTION))) {
                // Show DialogFragment
                this.handleShowDialogInteraction(data);
            } else if (FragmentInteraction.ACTION_SHOW_PROGRESS_DIALOG.equals(data.getString(FragmentInteraction.KEY_ACTION))) {
                // Show DialogFragment
                this.handleShowProgressDialogInteraction(data);
            } else if (FragmentInteraction.ACTION_SHOW_TRANSACTION_RESULT_DIALOG.equals(data.getString(FragmentInteraction.KEY_ACTION))) {
                // Show DialogFragment
                this.handleShowTransactionResultDialogInteraction(data);
            } else if (FragmentInteraction.ACTION_DISMISS_DIALOG.equals(data.getString(FragmentInteraction.KEY_ACTION))) {
                // Dismiss DialogFragment
                this.handleDismissDialogInteraction(data);
            } else if (FragmentInteraction.ACTION_UPDATE_MESSAGE.equals(data.getString(FragmentInteraction.KEY_ACTION))) {
                // Update message
                this.handleUpdateMessageInteraction(data);
            }
        }
    }

    /**
     * Handles an interaction request to show a new Fragment.
     *
     * @param data Interaction data.
     */
    private void handleShowFragmentInteraction(@NonNull Bundle data) {
        Fragment fragment = null;
        Class<Fragment> cls = null;

        try {
            cls = (Class<Fragment>) data.getSerializable(FragmentInteraction.KEY_FRAGMENT_CLASS);
            fragment = (Fragment) Class.forName(cls.getName()).newInstance();

            if (fragment != null) {
                fragment.setArguments(data);
                this.showFragment(fragment, data.getString(FragmentInteraction.KEY_FRAGMENT_TAG), true);
            }
        } catch (Exception e) {
            // Handle error while trying to create a new Fragment
            e.printStackTrace();
        }
    }

    /**
     * Handles an interaction request to show a new DialogFragment.
     *
     * @param data Interaction data.
     */
    private void handleShowDialogInteraction(@NonNull Bundle data) {
        this.dismissAllDialogs();

        this.mDialogFragment = new MessageDialogFragment();
        this.mDialogFragment.setArguments(data);
        this.mDialogFragment.show(this.getSupportFragmentManager(), "message");
    }

    /**
     * Handles an interaction request to show a new progress Dialog.
     *
     * @param data Interaction data.
     */
    private void handleShowProgressDialogInteraction(@NonNull Bundle data) {
        if (this.mProgressDialogFragment != null && this.mProgressDialogFragment.isVisible()) {
            this.mProgressDialogFragment.updateArguments(data);
        } else {
            this.mProgressDialogFragment = new ProgressDialogFragment();
            this.mProgressDialogFragment.setArguments(data);
            this.mProgressDialogFragment.show(this.getSupportFragmentManager(), "progress_dialog");
        }
    }

    /**
     * Handles an interaction request to show a transaction result Dialog.
     *
     * @param data Interaction data.
     */
    private void handleShowTransactionResultDialogInteraction(@NonNull Bundle data) {
        this.dismissAllDialogs();

        this.mTransactionResultDialogFragment = new TransactionResultDialogFragment();
        this.mTransactionResultDialogFragment.setArguments(data);
        this.mTransactionResultDialogFragment.show(this.getSupportFragmentManager(), "transaction_result");
    }

    /**
     * Handles an interaction request to dismiss Dialogs.
     *
     * @param data Interaction data.
     */
    private void handleDismissDialogInteraction(@Nullable Bundle data) {
        this.dismissAllDialogs();
    }

    /**
     * Updates the message being displayed on the DialogFragment.
     *
     * @param data Data where the new message is stored.
     */
    private void handleUpdateMessageInteraction(@NonNull Bundle data) {
        if (this.mDialogFragment != null && this.mDialogFragment.isVisible()) {
            this.mDialogFragment.updateMessage(data.getString(FragmentInteraction.KEY_DIALOG_MESSAGE));
        } else if (this.mProgressDialogFragment != null && this.mProgressDialogFragment.isVisible()) {
            this.mProgressDialogFragment.updateMessage(data.getString(FragmentInteraction.KEY_DIALOG_MESSAGE));
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Payment interruption
    // ---------------------------------------------------------------------------------------------


    @Override
    public void onAbort(@NonNull PlugPag plugpag) {
        Toast.makeText(this, "Abortando", Toast.LENGTH_SHORT).show();
        if (plugpag != null) {
            plugpag.abort();
        }
    }

    @Override
    public void onBluetoothStateChanged(int state) {
//        FragmentInteraction.dismissDialogs(this);
//        PlugPagManager.getInstance().getPlugPag().abort();
    }

}
