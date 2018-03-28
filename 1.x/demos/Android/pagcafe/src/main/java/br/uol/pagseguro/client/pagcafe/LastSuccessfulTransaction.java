package br.uol.pagseguro.client.pagcafe;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import br.uol.pagseguro.client.pagcafe.model.Transaction;

public final class LastSuccessfulTransaction {

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    public static Transaction transaction = null;

    // ---------------------------------------------------------------------------------------------
    // Transaction comparison
    // ---------------------------------------------------------------------------------------------

    /**
     * Checks if a Transaction is the same as the last successful transaction stored.
     *
     * @param transaction Transaction to be compared.
     * @return If the given Tranasction is the same as the last successful transaction stored.
     */
    public static final boolean equals(@Nullable Transaction transaction) {
        boolean equals = false;

        if (transaction == null && LastSuccessfulTransaction.transaction == null ||
                transaction == LastSuccessfulTransaction.transaction) {
            // Both objects are null or both objects point to the same memory position
            equals = true;
        } else if (transaction != null && LastSuccessfulTransaction.transaction != null) {
            // Compare the objects' attributes
            equals = TextUtils.equals(
                    transaction.getTransactionCode(),
                    LastSuccessfulTransaction.transaction.getTransactionCode());
        }

        return equals;
    }

}
