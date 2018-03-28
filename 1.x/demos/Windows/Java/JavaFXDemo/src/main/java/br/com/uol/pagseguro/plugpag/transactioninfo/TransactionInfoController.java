package br.com.uol.pagseguro.plugpag.transactioninfo;

import br.com.uol.pagseguro.plugpag.base.BaseController;
import br.com.uol.pagseguro.plugpag.Configuration;
import br.com.uol.pagseguro.plugpag.Executor;
import br.com.uol.pagseguro.plugpag.log.Logger;
import br.uol.pagseguro.client.plugpag.PlugPag;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

/**
 * Controller for the transaction info Stage.
 */
public class TransactionInfoController extends BaseController {

    // -----------------------------------------------------------------------------------------------------------------
    // Controls
    // -----------------------------------------------------------------------------------------------------------------

    @FXML
    private GridPane mPaneContent = null;

    @FXML
    private Label mLblTransactionStatus = null;

    @FXML
    private Label mLblMessage = null;

    @FXML
    private Label mLblTransactionCode = null;

    @FXML
    private Label mLblDate = null;

    @FXML
    private Label mLblTime = null;

    @FXML
    private Label mLblHostNsu = null;

    @FXML
    private Label mLblCardBrand = null;

    @FXML
    private Label mLblBin = null;

    @FXML
    private Label mLblHolder = null;

    @FXML
    private Label mLblUserReference = null;

    @FXML
    private Label mLblSerialNumber = null;

    @FXML
    private ProgressBar mPgbLoadingAnimation = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Initialization
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void init() {
        super.init();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Transaction query
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts a transaction query.
     */
    public void startTransactionQuery() {
        Executor.submit(() -> {
            PlugPag pp = null;
            int ret = 0;

            // Prepare the PlugPag reference
            pp = new PlugPag();
            pp.InitBTConnection(Configuration.BT_PORT);
            pp.SetVersionName(Configuration.APP_NAME, Configuration.APP_VERSION);

            try {
                // Query for the last transaction
                Logger.log(String.format("[Thread %d] Querying last transaction", Thread.currentThread().getId()));
                ret = pp.GetLastApprovedTransactionStatus();
            } catch (Exception e) {
                Logger.log(String.format("[Thread %d] Error while querying last transaction"));
                ret = -1;
            } finally {
                final int operationReturnvalue = ret;
                final PlugPag plugPag = pp;

                Platform.runLater(() -> {
                    // Display the query result
                    this.populateControls(operationReturnvalue, plugPag);
                });

                Logger.log(String.format("[Thread %d] Query finished with status %d", Thread.currentThread().getId(), ret));
            }
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Controls population
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Populates the Controls.
     *
     * @param returnValue Transaction querying return value.
     * @param pp          PlugPag instance used to fetch data to fill the Controls.
     */
    public void populateControls(int returnValue, PlugPag pp) {
        this.mPgbLoadingAnimation.setVisible(false);

        if (returnValue != PlugPag.RET_OK) {
            this.mLblTransactionStatus.setText(String.valueOf(returnValue));
        }

        if (pp != null && returnValue == PlugPag.RET_OK) {
            this.mLblMessage.setText(pp.getMessage());
            this.mLblTransactionCode.setText(pp.getTransactionCode());
            this.mLblDate.setText(pp.getDate());
            this.mLblTime.setText(pp.getTime());
            this.mLblHostNsu.setText(pp.getHostNsu());
            this.mLblCardBrand.setText(pp.getCardBrand());
            this.mLblBin.setText(pp.getBin());
            this.mLblHolder.setText(pp.getHolder());
            this.mLblUserReference.setText(pp.getUserReference());
            this.mLblSerialNumber.setText(pp.getTerminalSerialNumber());
        } else {
            this.mPaneContent.setVisible(false);
        }
    }

}
