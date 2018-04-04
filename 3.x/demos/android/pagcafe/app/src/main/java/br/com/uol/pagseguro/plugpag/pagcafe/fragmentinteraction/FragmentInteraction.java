package br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;

import br.com.uol.pagseguro.plugpag.pagcafe.OnAbortListener;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;

public final class FragmentInteraction {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    public static final String KEY_ACTION = "FRAGMENT_INTERACTION_ACTION";
    public static final String KEY_FRAGMENT_CLASS = "KEY_FRAGMENT_CLASS";
    public static final String KEY_FRAGMENT_TAG = "KEY_FRAGMENT_TAG";
    public static final String KEY_DIALOG_MESSAGE = "KEY_DIALOG_MESSAGE";
    public static final String KEY_DIALOG_TITLE = "KEY_DIALOG_TITLE";
    public static final String KEY_DIALOG_TYPE = "KEY_DIALOG_TYPE";
    public static final String KEY_DIALOG_DISMISSABLE = "KEY_DIALOG_DISMISSABLE";
    public static final String KEY_DIALOG_TRANSACTION_RESULT = "KEY_DIALOG_TRANSACTION_RESULT";
    public static final String KEY_DIALOG_ABORT_LISTENER = "KEY_DIALOG_ABORT_LISTENER";

    public static final String ACTION_SHOW_FRAGMENT = "ACTION_SHOW_FRAGMENT";
    public static final String ACTION_SHOW_DIALOG = "ACTION_SHOW_DIALOG";
    public static final String ACTION_SHOW_PROGRESS_DIALOG = "ACTION_SHOW_PROGRESS_DIALOG";
    public static final String ACTION_UPDATE_MESSAGE = "ACTION_UPDATE_MESSAGE";
    public static final String ACTION_SHOW_TRANSACTION_RESULT_DIALOG = "ACTION_SHOW_TRANSACTION_RESULT_DIALOG";
    public static final String ACTION_DISMISS_DIALOG = "ACTION_DISMISS_DIALOG";

    public static final int DIALOG_TYPE_NORMAL = 0;
    public static final int DIALOG_TYPE_ERROR = 1;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Empty constructor to prevent instantiation.
     */
    private FragmentInteraction() {
    }

    // ---------------------------------------------------------------------------------------------
    // Show dialog
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an interaction to show a ProgressDialog.
     *
     * @param listener Fragment interaction listener.
     * @param title    Dialog's title.
     * @param message  Dialog's message.
     */
    public static final void showProgressDialog(@NonNull OnFragmentInteractionListener listener,
                                                @NonNull String title,
                                                @NonNull String message) {
        FragmentInteraction.showProgressDialog(listener, title, message, null);
    }

    /**
     * Creates an interaction to show a ProgressDialog.
     * If the <code>abortButtonClickListener</code> parameter is not null, shows the "abort" button.
     *
     * @param listener        Fragment interaction listener.
     * @param title           Dialog's title.
     * @param message         Dialog's message.
     * @param abortButtonClickListener "Abort" button click listener.
     */
    public static final void showProgressDialog(@NonNull OnFragmentInteractionListener listener,
                                                @NonNull String title,
                                                @NonNull String message,
                                                @Nullable OnAbortListener abortButtonClickListener) {
        Bundle args = null;

        if (listener == null) {
            throw new NullPointerException("Missing FragmentInteraction listener");
        }

        if (TextUtils.isEmpty(title)) {
            throw new RuntimeException("Missing Dialog's title");
        }

        if (TextUtils.isEmpty(message)) {
            throw new RuntimeException("Missing Dialog's message");
        }

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_PROGRESS_DIALOG);
        args.putString(FragmentInteraction.KEY_DIALOG_TITLE, title);
        args.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);
        args.putBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, false);
        args.putSerializable(FragmentInteraction.KEY_DIALOG_ABORT_LISTENER, abortButtonClickListener);
        listener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
    }

    // ---------------------------------------------------------------------------------------------
    // Show message
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an interaction to show a message.
     *
     * @param listener Fragment interaction listener.
     * @param type     Dialog type.
     * @param title    Dialog's title.
     * @param message  Dialog's message.
     */
    public static final void showMessage(@NonNull OnFragmentInteractionListener listener,
                                         int type,
                                         @NonNull String title,
                                         @NonNull String message) {
        Bundle args = null;

        if (listener == null) {
            throw new NullPointerException("Missing FragmentInteraction listener");
        }

        if (TextUtils.isEmpty(title)) {
            throw new RuntimeException("Missing Dialog's title");
        }

        if (TextUtils.isEmpty(message)) {
            throw new RuntimeException("Missing Dialog's message");
        }

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_DIALOG);
        args.putInt(FragmentInteraction.KEY_DIALOG_TYPE, type);
        args.putString(FragmentInteraction.KEY_DIALOG_TITLE, title);
        args.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);
        listener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
    }

    /**
     * Creates an interaction to show an error message.
     *
     * @param listener Fragment interaction listener.
     * @param title    Dialog's title.
     * @param message  Dialog's message.
     */
    public static final void showErrorMessage(@NonNull OnFragmentInteractionListener listener,
                                              @NonNull String title,
                                              @NonNull String message) {
        FragmentInteraction.showMessage(
                listener,
                FragmentInteraction.DIALOG_TYPE_ERROR,
                title,
                message);
    }

    /**
     * Creates an interaction to show a normal message.
     *
     * @param listener Fragment interaction listener.
     * @param title    Dialog's title.
     * @param message  Dialog's message.
     */
    public static final void showNormalMessage(@NonNull OnFragmentInteractionListener listener,
                                               @NonNull String title,
                                               @NonNull String message) {
        FragmentInteraction.showMessage(
                listener,
                FragmentInteraction.DIALOG_TYPE_NORMAL,
                title,
                message);
    }

    // ---------------------------------------------------------------------------------------------
    // Show update
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an interaction to update a message.
     *
     * @param listener Fragment interaction listener.
     * @param title    Dialog's title.
     * @param message  Dialog's message.
     */
    public static final void updateMessage(@NonNull OnFragmentInteractionListener listener,
                                           @NonNull String title,
                                           @NonNull String message) {
        Bundle args = null;

        if (listener == null) {
            throw new NullPointerException("Missing FragmentInteraction listener");
        }

        if (TextUtils.isEmpty(title)) {
            throw new RuntimeException("Missing Dialog's title");
        }

        if (TextUtils.isEmpty(message)) {
            throw new RuntimeException("Missing Dialog's message");
        }

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_UPDATE_MESSAGE);
        args.putString(FragmentInteraction.KEY_DIALOG_TITLE, title);
        args.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);
        listener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
    }

    // ---------------------------------------------------------------------------------------------
    // Show new Fragment
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an interaction to show a new Fragment
     *
     * @param listener      Fragment interaction listener.
     * @param fragmentClass Class of the Fragment to be displayed.
     *                      The fragment must implement {@link Serializable}
     * @param tag           Fragment's identification tag.
     */
    public static final void showFragment(@NonNull OnFragmentInteractionListener listener,
                                          @NonNull Serializable fragmentClass,
                                          @Nullable String tag) {
        Bundle args = null;

        if (listener == null) {
            throw new NullPointerException("Missing FragmentInteraction listener");
        }

        if (fragmentClass == null) {
            throw new NullPointerException("Missing Fragment's class");
        }

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_FRAGMENT);
        args.putSerializable(FragmentInteraction.KEY_FRAGMENT_CLASS, fragmentClass);

        if (!TextUtils.isEmpty(tag)) {
            args.putString(FragmentInteraction.KEY_FRAGMENT_TAG, tag);
        }

        listener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
    }

    // ---------------------------------------------------------------------------------------------
    // Show new Fragment
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an interaction to show a new Fragment
     *
     * @param listener          Fragment interaction listener.
     * @param title             Dialog title.
     * @param message           Dialog message.
     * @param transactionResult Transaction result to be displayed.
     */
    public static final void showTransactionResult(@NonNull OnFragmentInteractionListener listener,
                                                   @Nullable String title,
                                                   @Nullable String message,
                                                   @NonNull PlugPagTransactionResult transactionResult) {
        Bundle args = null;

        if (listener == null) {
            throw new NullPointerException("Missing FragmentInteraction listener");
        }

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_SHOW_TRANSACTION_RESULT_DIALOG);
        args.putInt(FragmentInteraction.KEY_DIALOG_TYPE, FragmentInteraction.DIALOG_TYPE_NORMAL);
        args.putSerializable(FragmentInteraction.KEY_DIALOG_TRANSACTION_RESULT, transactionResult);
        args.putBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, true);

        if (!TextUtils.isEmpty(title)) {
            args.putString(FragmentInteraction.KEY_DIALOG_TITLE, title);
        }

        if (!TextUtils.isEmpty(message)) {
            args.putString(FragmentInteraction.KEY_DIALOG_MESSAGE, message);
        }

        listener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
    }

    // ---------------------------------------------------------------------------------------------
    // Dialogs dismiss
    // ---------------------------------------------------------------------------------------------

    /**
     * Dismisses visible Dialogs.
     *
     * @param listener Fragment interaction listener.
     */
    public static final void dismissDialogs(@NonNull OnFragmentInteractionListener listener) {
        Bundle args = null;

        if (listener == null) {
            throw new NullPointerException("Missing FragmentInteraction listener");
        }

        args = new Bundle();
        args.putString(FragmentInteraction.KEY_ACTION, FragmentInteraction.ACTION_DISMISS_DIALOG);
        listener.onInteract(new OnFragmentInteractionListener.FragmentInteractionInfo(args));
    }

}
