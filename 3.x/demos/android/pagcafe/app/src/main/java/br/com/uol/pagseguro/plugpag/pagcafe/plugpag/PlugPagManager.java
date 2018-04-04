package br.com.uol.pagseguro.plugpag.pagcafe.plugpag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagAppIdentification;

public final class PlugPagManager {

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    /**
     * Singleton instance.
     */
    private static PlugPagManager sInstance = null;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private PlugPagAppIdentification mAppIdentification = null;
    private PlugPag mPlugPag = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new PlugPagManager instance.
     *
     * @param context Context used to access application and device information.
     * @param appIdentification PlugPag app identification.
     */
    private PlugPagManager(@NonNull Context context, @NonNull PlugPagAppIdentification appIdentification) {
        this.mAppIdentification = appIdentification;
        this.mPlugPag = new PlugPag(context, appIdentification);
    }

    // ---------------------------------------------------------------------------------------------
    // Singleton
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the singleton instance of the PlugPagManager.
     *
     * @param context Context used to access application and device information.
     * @return Singleton instance of the PlugPagManager.
     */
    public static final PlugPagManager getInstance(@Nullable Context context) {
        if (PlugPagManager.sInstance == null && context != null) {
            PlugPagManager.sInstance = new PlugPagManager(context, AppIdentification.getInstance());

        }

        return PlugPagManager.sInstance;
    }

    /**
     * Returns the singleton instance of the PlugPagManager.
     *
     * @return Singleton instance of the PlugPagManager.
     */
    public static final PlugPagManager getInstance() {
        return PlugPagManager.getInstance(null);
    }

    // ---------------------------------------------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------------------------------------------

    /**
     * Initializes the PlugPagManager instance.
     *
     * @param context Context used to access application and device information.
     */
    public static final void initialize(@NonNull Context context) {
        PlugPagManager.getInstance(context);
    }

    // ---------------------------------------------------------------------------------------------
    // Resources release
    // ---------------------------------------------------------------------------------------------

    /**
     * Releases resources.
     */
    public static final void release() {
        if (PlugPagManager.sInstance != null) {
            PlugPagManager.sInstance.mPlugPag = null;
            PlugPagManager.sInstance.mAppIdentification = null;
            PlugPagManager.sInstance = null;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Getter
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the PlugPag reference.
     *
     * @return PlugPag reference.
     */
    public PlugPag getPlugPag() {
        return this.mPlugPag;
    }

}
