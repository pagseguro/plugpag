package br.com.uol.pagseguro.plugpag.transactioninfo;

import br.com.uol.pagseguro.plugpag.base.BaseController;
import br.com.uol.pagseguro.plugpag.Tuple;
import br.com.uol.pagseguro.plugpag.base.Coordinator;
import br.uol.pagseguro.client.plugpag.PlugPag;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Coordinator used to display the transaction info Stage.
 */
public final class TransactionInfoCoordinator {

    // -----------------------------------------------------------------------------------------------------------------
    // Content display
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Shows an empty query transaction Scene.
     * The displayed Scene will start a task to load the last transaction's result.
     *
     * @param classLoader ClassLoader used to access application resources.
     * @param owner       Stage that will own the created Scene.
     */
    public static final void show(ClassLoader classLoader, Stage owner) {
        Tuple<Stage, Scene, BaseController> tuple = null;

        tuple = Coordinator.getInstance().createModal(classLoader, owner, "ui/transactioninfo.fxml");
        ((TransactionInfoController) tuple.getThird()).startTransactionQuery();
        tuple.getFirst().showAndWait();
    }

    /**
     * Shows a transaction result Scene and fills it with the given result data.
     *
     * @param classLoader ClassLoader used to access application resources.
     * @param owner       Stage that will own the created Scene.
     * @param result      Result status code.
     * @param plugPag     PlugPag reference with data used to fill the Controls
     */
    public static final void show(ClassLoader classLoader, Stage owner, int result, PlugPag plugPag) {
        Tuple<Stage, Scene, BaseController> tuple = null;

        tuple = Coordinator.getInstance().createModal(classLoader, owner, "ui/transactioninfo.fxml");
        ((TransactionInfoController) tuple.getThird()).populateControls(result, plugPag);
        tuple.getFirst().showAndWait();
    }

}
