package br.com.uol.pagseguro.plugpag.menu;

import br.com.uol.pagseguro.plugpag.base.BaseController;
import br.com.uol.pagseguro.plugpag.Configuration;
import br.com.uol.pagseguro.plugpag.Executor;
import br.com.uol.pagseguro.plugpag.log.Logger;
import br.com.uol.pagseguro.plugpag.pinpad.PinpadCoordinator;
import br.com.uol.pagseguro.plugpag.transactioninfo.TransactionInfoCoordinator;
import br.uol.pagseguro.client.plugpag.PlugPag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the menu Stage.
 */
public class MenuController extends BaseController {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    @FXML
    private Button mBtnPay = null;

    @FXML
    private Button mBtnCancelPayment = null;

    @FXML
    private Button mBtnQueryLastSuccessfulTransaction = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Initialization
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void init() {
        super.init();

        this.setButtonsEnabled(false);

        Executor.submit(() -> {
            // Load libraries
            Logger.log("[DLL] Loading BTSerial");
            System.loadLibrary("BTSerial");
            Logger.log("[DLL] BTSerial loaded");
            Logger.log("[DLL] Loading PPPagSeguro");
            System.loadLibrary("PPPagSeguro");
            Logger.log("[DLL] PPPagSeguro loaded");

            Platform.runLater(() -> {
                MenuController.this.setButtonsEnabled(true);
            });
        });
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Button click listeners
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Handles click events on the Button that starts pinpad Scene.
     *
     * @param event Event sent.
     */
    @FXML
    private void onPayClick(ActionEvent event) {
        Logger.log("Pay Button clicked");

        if (this.areButtonsEnabled()) {
            // Disable all Buttons
            this.setButtonsEnabled(false);

            try {
                // Show the pinpad Scene and wait until it is closed
                PinpadCoordinator.show(this.getClass().getClassLoader(), this.getStage());
            } catch (Exception e) {
                Logger.log("Could not load pinpad scene");
            } finally {
                // Enable all Buttons
                this.setButtonsEnabled(true);
            }
        } else {
            Logger.log("All Buttons are disabled");
        }
    }

    /**
     * Handles click events on the Button that starts payment cancellation routine.
     *
     * @param event Event sent.
     */
    @FXML
    private void onCancelPaymentClick(ActionEvent event) {
        Logger.log("Cancel pinpad Button clicked");

        if (this.areButtonsEnabled()) {
            // Disable all Buttons
            this.setButtonsEnabled(false);

            // Start the task to make the terminal cancel a payment
            Executor.submit(() -> {
                PlugPag pp = null;
                int ret = 0;

                pp = new PlugPag();
                pp.InitBTConnection(Configuration.BT_PORT);
                pp.SetVersionName(Configuration.APP_NAME, Configuration.APP_VERSION);

                try {
                    Logger.log(String.format("[Thread %d] Cancelling last transaction", Thread.currentThread().getId()));
                    ret = pp.CancelTransaction();
                } catch (Exception e) {
                    Logger.log(String.format("[Thread %d] Error while cancelling last transaction"));
                    ret = -1;
                } finally {
                    // Create final variables to be accessed in a Runnable
                    final int operationReturnValue = ret;
                    final PlugPag plugPag = pp;

                    // Define the Runnable to run on the main thread
                    Platform.runLater(() -> {
                        // Show the transaction result Scene
                        TransactionInfoCoordinator.show(
                                MenuController.this.getClass().getClassLoader(),
                                this.getStage(), operationReturnValue, plugPag);

                        // Enable Buttons
                        MenuController.this.setButtonsEnabled(true);
                    });

                    Logger.log(String.format("[Thread %d] Query finished with status %d", Thread.currentThread().getId(), ret));
                }
            });
        } else {
            Logger.log("All Buttons are disabled");
        }
    }

    /**
     * Handles click events on the Button that starts the query transaction Scene.
     *
     * @param event Event sent.
     */
    @FXML
    public void onQueryLastTransactionClick(ActionEvent event) {
        Logger.log("Query last transaction Button clicked");

        if (this.areButtonsEnabled()) {
            this.setButtonsEnabled(false);

            try {
                // Show the query transaction Scene
                TransactionInfoCoordinator.show(this.getClass().getClassLoader(), this.getStage());
            } catch (Exception e) {
                Logger.log("Could not load transaction info Scene");
            } finally {
                this.setButtonsEnabled(true);
            }
        } else {
            Logger.log("All Buttons are disabled");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Enable/disable Buttons
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Sets the Buttons availability.
     *
     * @param enabled If the Buttons must be enabled.
     */
    private void setButtonsEnabled(boolean enabled) {
        this.mBtnPay.setDisable(!enabled);
        this.mBtnCancelPayment.setDisable(!enabled);
        this.mBtnQueryLastSuccessfulTransaction.setDisable(!enabled);
    }

    /**
     * Checks if the Buttons are enabled.
     *
     * @return If the Buttons are enabled.
     */
    private boolean areButtonsEnabled() {
        return !this.mBtnPay.isDisabled() &&
                !this.mBtnCancelPayment.isDisabled() &&
                !this.mBtnQueryLastSuccessfulTransaction.isDisabled();
    }

}
