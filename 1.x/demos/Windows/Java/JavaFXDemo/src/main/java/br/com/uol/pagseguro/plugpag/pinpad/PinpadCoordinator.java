package br.com.uol.pagseguro.plugpag.pinpad;

import br.com.uol.pagseguro.plugpag.base.BaseController;
import br.com.uol.pagseguro.plugpag.Tuple;
import br.com.uol.pagseguro.plugpag.base.Coordinator;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Coordinator used to display the pinpad Stage.
 */
public final class PinpadCoordinator {

    // -----------------------------------------------------------------------------------------------------------------
    // Content display
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Shows the pinpad Scene in a modal Window.
     *
     * @param classLoader ClassLoader used to access application resources.
     * @param owner       Stage that will own the pinpad Scene.
     */
    public static final void show(ClassLoader classLoader, Stage owner) {
        Tuple<Stage, Scene, BaseController> tuple = null;

        tuple = Coordinator.getInstance().createModal(classLoader, owner, "ui/pinpad.fxml");
        tuple.getFirst().showAndWait();
    }

}
