package br.com.uol.pagseguro.plugpag.base;

import javafx.stage.Stage;

/**
 * Base for the application's controllers.
 */
public abstract class BaseController {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private Stage mStage = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Getters/Setters
    // -----------------------------------------------------------------------------------------------------------------

    public final Stage getStage() {
        return this.mStage;
    }

    public final void setStage(Stage stage) {
        this.mStage = stage;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Initialization
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the BaseController.
     */
    public void init() {

    }

}
