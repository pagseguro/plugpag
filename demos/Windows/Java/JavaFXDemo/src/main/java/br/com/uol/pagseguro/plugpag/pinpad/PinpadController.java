package br.com.uol.pagseguro.plugpag.pinpad;

import br.com.uol.pagseguro.plugpag.base.BaseController;
import br.com.uol.pagseguro.plugpag.Configuration;
import br.com.uol.pagseguro.plugpag.Executor;
import br.com.uol.pagseguro.plugpag.log.Logger;
import br.com.uol.pagseguro.plugpag.transactioninfo.TransactionInfoCoordinator;
import br.uol.pagseguro.client.plugpag.PlugPag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 * Controller for the pinpad Stage.
 */
public class PinpadController extends BaseController implements Initializable {

    // -----------------------------------------------------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------------------------------------------------

    private static final int MAX_DIGITS = 9;

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    @FXML
    private Pane mPane = null;

    @FXML
    private Label mLblStatus = null;

    @FXML
    private Label mLblValue = null;

    @FXML
    private ProgressBar mPgbLoadingAnimation = null;

    private boolean mAreButtonsEnabled = true;

    // -----------------------------------------------------------------------------------------------------------------
    // Getters/setters
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Logger.log("Pinpad Stage started");

        // Set keyboard events handler
        this.mPane.setOnKeyTyped((event) -> {
            if (PinpadController.this.mAreButtonsEnabled) {
                char firstChar = event.getCharacter().charAt(0);

                if (firstChar == 8 || firstChar == 127) {
                    // Backspace or delete
                    this.eraseDigit();
                } else if (firstChar == 27) {
                    // ESC
                    this.getStage().close();
                } else if (firstChar == 13 || firstChar == 10) {
                    // ENTER
                    this.proceedToPayment();
                } else if (firstChar >= '0' && firstChar <= '9') {
                    // Digits
                    this.concatenateNumber(event.getCharacter());
                }
            }
        });

        this.mPgbLoadingAnimation.setVisible(false);
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Button clicks
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Handles Button clicks.
     *
     * @param event Generated event.
     */
    @FXML
    private void onButtonClick(ActionEvent event) {
        String value = null;

        if (this.mAreButtonsEnabled && event != null && event.getSource() instanceof Button) {
            value = ((Button) event.getSource()).getText();

            if (value != null && value.length() > 0) {
                if (value.charAt(0) >= '0' && value.charAt(0) <= '9') {
                    Logger.log("Concatenating number %s", value);
                    this.concatenateNumber(value);
                } else if (value.charAt(0) == '<') {
                    Logger.log("Erasing value");
                    this.eraseDigit();
                } else if (value.charAt(0) == '>') {
                    Logger.log("Proceeding to payment");
                    this.proceedToPayment();
                }
            }
        }
    }

    /**
     * Concatenates a number and reformats the final value.
     *
     * @param value Number to be concatenated.
     */
    private void concatenateNumber(String value) {
        String newValue = null;

        newValue = this.formatNumber(this.mLblValue.getText() + value);

        if (newValue.length() <= PinpadController.MAX_DIGITS) {
            this.mLblValue.setText(newValue);
            this.mLblStatus.setText("");
        } else {
            this.mLblStatus.setText("Número máximo de dígitos alcançado");
        }
    }

    /**
     * Erases a digit.
     */
    private void eraseDigit() {
        String original = null;
        String newValue = null;

        original = this.mLblValue.getText();
        newValue = this.formatNumber(original.substring(0, original.length() - 1));

        if (newValue.length() <= PinpadController.MAX_DIGITS) {
            this.mLblValue.setText(newValue);
            this.mLblStatus.setText("");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Content formatting
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Formats a number.
     *
     * @param value Value to be formatted.
     * @return Formatted number.
     */
    private String formatNumber(String value) {
        String digitsOnly = null;
        NumberFormat formatter = null;
        String finalValue = null;

        formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMinimumIntegerDigits(1);
        formatter.setGroupingUsed(false);

        digitsOnly = value.replaceAll("[^0-9]", "");
        finalValue = formatter.format((float) Integer.valueOf(digitsOnly) / 100.0);

        return finalValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Payment
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Proceeds to the payment Scene.
     */
    private void proceedToPayment() {
        this.mLblStatus.setText("Aguardando pagamento");
        this.mPgbLoadingAnimation.setVisible(true);
        this.mAreButtonsEnabled = false;

        Executor.submit(() -> {
            String amount = this.mLblValue.getText().replaceAll("[^0-9]", "");
            int installmentType = PlugPag.A_VISTA;
            int installment = 1;
            int method = PlugPag.CREDIT;
            int ret = -1;

            // Prepare the PlugPag reference
            final PlugPag pp = new PlugPag();
            pp.InitBTConnection(Configuration.BT_PORT);
            pp.SetVersionName(Configuration.APP_NAME, Configuration.APP_VERSION);

            // Start the payment
            Logger.log(String.format("[Thread %d] Starting payment of value: %s ", Thread.currentThread().getId(), amount));
            ret = pp.SimplePaymentTransaction(method, installmentType, installment, amount, Configuration.USER_REFERENCE);
            Logger.log(String.format("[Thread %d] Payment finished with result: %d", Thread.currentThread().getId(), ret));

            final int returnValue = ret;

            Platform.runLater(() -> {
                // Show the transaction result
                TransactionInfoCoordinator.show(
                        PinpadController.this.getClass().getClassLoader(),
                        this.getStage(), returnValue, pp);

                // Prepare for the next payment
                PinpadController.this.mLblStatus.setText("Pronto");
                PinpadController.this.mPgbLoadingAnimation.setVisible(false);
                PinpadController.this.mLblValue.setText("0,00");
                this.mAreButtonsEnabled = true;
            });
        });
    }

}
