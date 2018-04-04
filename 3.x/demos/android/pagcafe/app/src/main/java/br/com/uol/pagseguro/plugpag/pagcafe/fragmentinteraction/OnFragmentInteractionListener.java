package br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface OnFragmentInteractionListener {

    /**
     * Called when an interaction is requested.
     *
     * @param interactionInfo Interaction information.
     */
    void onInteract(@NonNull FragmentInteractionInfo interactionInfo);

    // ---------------------------------------------------------------------------------------------
    // Interaction data
    // ---------------------------------------------------------------------------------------------

    final class FragmentInteractionInfo {

        // -----------------------------------------------------------------------------------------
        // Instance attributes
        // -----------------------------------------------------------------------------------------

        private Bundle mArguments = null;

        // -----------------------------------------------------------------------------------------
        // Constructors
        // -----------------------------------------------------------------------------------------

        /**
         * Creates a new FragmentInteractionInfo with the given data.
         *
         * @param data     Data used to handle the interaction.
         */
        public FragmentInteractionInfo(@Nullable Bundle data) {
            this.mArguments = new Bundle(data);
        }

        // -----------------------------------------------------------------------------------------
        // Getters
        // -----------------------------------------------------------------------------------------

        /**
         * Returns a reference to a copy of a data Bundle.
         *
         * @return Reference of a copy of the data Bundle.
         */
        public Bundle getData() {
            Bundle data = null;

            if (this.mArguments != null) {
                data = new Bundle(this.mArguments);
            } else {
                data = new Bundle();
            }

            return data;
        }

    }

}
