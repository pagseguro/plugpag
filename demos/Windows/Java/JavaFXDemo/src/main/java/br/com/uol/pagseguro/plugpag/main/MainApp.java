package br.com.uol.pagseguro.plugpag.main;

import br.com.uol.pagseguro.plugpag.PathAdapter;
import br.com.uol.pagseguro.plugpag.base.BaseController;
import br.com.uol.pagseguro.plugpag.Executor;
import br.com.uol.pagseguro.plugpag.ExitCodes;
import br.com.uol.pagseguro.plugpag.log.Logger;
import br.com.uol.pagseguro.plugpag.menu.MenuController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application.
 * Displays the main Window to prepare any other Stages and Scenes.
 */
public class MainApp extends Application {

    // -----------------------------------------------------------------------------------------------------------------
    // Single initialization
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Adds relative paths to load libraries
     */
    static {
        // Path used when running the app from an IDE or via command line from the root of the project
        new PathAdapter().addPathRelativeToApp("lib");

        // Path used when running the app from gradle using "gradle jfxRun"
        new PathAdapter().addPathRelativeToApp(String.format(".%1$s..%1$s..%1$s..%1$slib", PathAdapter.DIRECTORY_SEPARATOR));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private Stage mPrimaryStage = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Initialization
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts the application in an old-fashioned way.
     *
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the application.
     *
     * @param primaryStage Primary Stage reference.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.setupPrimaryStage(primaryStage);
        this.showMenu();

        primaryStage.setOnCloseRequest((event) -> {
            Executor.release();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Setups the primary Stage.
     *
     * @param stage Primary Stage reference.
     * @throws Exception
     */
    private void setupPrimaryStage(Stage stage) throws Exception {
        Parent root = null;

        root = FXMLLoader.load(this.getClass().getClassLoader().getResource("ui/main.fxml"));

        stage.setTitle("PlugPag JavaFX Demo");
        stage.setScene(new Scene(root));
        stage.setResizable(false);

        // Show primary stage
        stage.show();

        this.mPrimaryStage = stage;
    }

    /**
     * Shows the menu Scene.
     */
    public void showMenu() {
        Parent root = null;
        FXMLLoader loader = null;

        try {
            loader = new FXMLLoader(this.getClass().getClassLoader().getResource("ui/menu.fxml"));
            root = loader.load();
            ((MenuController) loader.getController()).setStage(this.mPrimaryStage);
            ((BaseController) loader.getController()).init();
            this.mPrimaryStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(ExitCodes.ERROR_LOADING_FXML);
        }
    }

}
