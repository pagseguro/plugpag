package br.com.uol.pagseguro.plugpag.base;

import br.com.uol.pagseguro.plugpag.Tuple;
import br.com.uol.pagseguro.plugpag.log.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Stage and Scene coordinator with implementation of common tasks to display Windows.
 */
public final class Coordinator {

    // -----------------------------------------------------------------------------------------------------------------
    // Class attributes
    // -----------------------------------------------------------------------------------------------------------------

    private static Coordinator sInstance = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the PinpadCoordinator singleton instance.
     *
     * @return Coordinator singleton instance.
     */
    public static final Coordinator getInstance() {
        if (Coordinator.sInstance == null) {
            Coordinator.sInstance = new Coordinator();
        }

        return Coordinator.sInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Show Scene
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a modal Stage.
     *
     * @param classLoader ClassLoader used to access the application resources.
     * @param owner       Stage owner of the Stage being created.
     * @param fxmlUri     URI of the FXML to be loaded into the Scene that will be put into the created Stage.
     * @return Tuple with the Stage, Scene and Controller created, in this order.
     */
    public final Tuple<Stage, Scene, BaseController> createModal(ClassLoader classLoader, Stage owner, String fxmlUri) {
        Stage stage = null;
        Scene scene = null;
        FXMLLoader loader = null;
        BaseController controller = null;

        try {
            stage = new Stage();

            if (owner != null) {
                // Set stage modality if an owner is provided
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(owner);
            }

            // Load the FXML
            loader = new FXMLLoader(classLoader.getResource(fxmlUri));
            scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.setResizable(false);
            controller = loader.getController();

            if (controller != null) {
                // Prepare the controller
                controller.setStage(stage);
                controller.init();
            }
        } catch (IOException e) {
            Logger.log("Error while loading modal Scene");
            e.printStackTrace();
        }

        return new Tuple<>(stage, scene, controller);
    }

}
