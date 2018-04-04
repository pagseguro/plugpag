package br.com.uol.pagseguro.plugpag.pagcafe.transaction;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TransactionStore {

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    /**
     * Singleton instance.
     */
    private static TransactionStore sInstance = null;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Map<String, List<TransactionData>> mTransactions = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new TransactionStore.
     */
    private TransactionStore() {
        this.mTransactions = new HashMap<>();
    }

    // ---------------------------------------------------------------------------------------------
    // Singleton
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the singleton instance of the TransactionStore.
     *
     * @return Singleton instance of the TransactionStore.
     */
    private static final TransactionStore getInstance() {
        if (TransactionStore.sInstance == null) {
            TransactionStore.sInstance = new TransactionStore();
        }

        return TransactionStore.sInstance;
    }

    // ---------------------------------------------------------------------------------------------
    // Filtering
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a list of TransactionData for the given device name.
     *
     * @param deviceName Device name used to filter the list of TransactionData.
     * @return List of TransactionData found.
     */
    public static final List<TransactionData> getData(@NonNull String deviceName) {
        List<TransactionData> transactions = null;
        TransactionStore store = null;

        store = TransactionStore.getInstance();

        if (store.mTransactions.containsKey(deviceName)) {
            transactions = new ArrayList<>(store.mTransactions.get(deviceName));
        } else {
            transactions = new ArrayList<>();
        }

        return transactions;
    }

    // ---------------------------------------------------------------------------------------------
    // Adding
    // ---------------------------------------------------------------------------------------------

    /**
     * Adds a transaction data to the current store.
     *
     * @param deviceName Device name used to store the TransactionData.
     * @param data       TransactionData to be stored.
     */
    public static final void addData(@NonNull String deviceName, @NonNull TransactionData data) {
        TransactionStore store = null;

        store = TransactionStore.getInstance();

        if (!store.mTransactions.containsKey(deviceName)) {
            store.mTransactions.put(deviceName, new ArrayList<TransactionData>());
        }

        if (!store.mTransactions.get(deviceName).contains(data)) {
            store.mTransactions.get(deviceName).add(data);
        }
    }

}
