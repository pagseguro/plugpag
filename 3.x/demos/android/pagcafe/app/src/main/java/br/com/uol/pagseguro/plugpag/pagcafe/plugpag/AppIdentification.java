package br.com.uol.pagseguro.plugpag.pagcafe.plugpag;

import br.com.uol.pagseguro.plugpag.PlugPagAppIdentification;


public final class AppIdentification extends PlugPagAppIdentification {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    private static final String APP_NAME = "PagCafe";
    private static final String APP_VERSION = "3.0.0";

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    /**
     * Singleton instance.
     */
    private static AppIdentification sInstance = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of the AppIdentification.
     */
    private AppIdentification() {
        super(AppIdentification.APP_NAME, AppIdentification.APP_VERSION);
    }

    // ---------------------------------------------------------------------------------------------
    // Singleton
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the singleton instance of the application identification.
     *
     * @return Singleton instance of the application identification.
     */
    public static final AppIdentification getInstance() {
        if (AppIdentification.sInstance == null) {
            AppIdentification.sInstance = new AppIdentification();
        }

        return AppIdentification.sInstance;
    }

}
