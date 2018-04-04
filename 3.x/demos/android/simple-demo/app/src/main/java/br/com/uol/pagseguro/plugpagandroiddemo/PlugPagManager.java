package br.com.uol.pagseguro.plugpagandroiddemo;

import android.content.Context;
import android.support.annotation.NonNull;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagAppIdentification;

public class PlugPagManager {

    // -----------------------------------------------------------------------------------------------------------------
    // Class attributes
    // -----------------------------------------------------------------------------------------------------------------

    private static PlugPagManager sInstance = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private PlugPag mPlugPag = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new PlugPagManager instance.<br />
     * This instance is meant to be singleton.
     *
     * @param appContext Application reference.
     */
    private PlugPagManager(@NonNull Context appContext) {
        PlugPagAppIdentification appIdentification = null;

        if (appContext == null) {
            throw new RuntimeException("Context reference cannot be null");
        }

        appIdentification = new PlugPagAppIdentification(
                appContext.getString(R.string.plugpag_app_identification),
                appContext.getString(R.string.plugpag_app_version));
        this.mPlugPag = new PlugPag(appContext, appIdentification);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Singleton accessor
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new PlugPagManager instance if none exists.
     *
     * @param appContext Application reference.
     * @return PlugPagManager singleton reference.
     */
    public static final PlugPagManager create(@NonNull Context appContext) {
        if (appContext == null) {
            throw new RuntimeException("Context reference cannot be null");
        }

        if (PlugPagManager.sInstance == null) {
            PlugPagManager.sInstance = new PlugPagManager(appContext);
        }

        return PlugPagManager.sInstance;
    }

    /**
     * Returns the PlugPagManager singleton reference.
     *
     * @return PlugPagManager singleton reference.
     */
    public static final PlugPagManager getInstance() {
        if (PlugPagManager.sInstance == null) {
            throw new RuntimeException("PlugPagManager not instantiated");
        }

        return PlugPagManager.sInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // PlugPag accessor
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the PlugPag reference.
     *
     * @return PlugPag reference.
     */
    public final PlugPag getPlugPag() {
        return this.mPlugPag;
    }

}
