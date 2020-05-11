package graphics.directories;

import java.io.*;
import controllers.commands.*;
import graphics.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;

public abstract class DirectoryGraphics {
    protected CommandRunner runner;
    protected GraphicsController controller;
    protected Scene scene;
    @FXML
    protected Button homeButton;
    @FXML
    protected MenuItem logoutButton, exitButton;

    protected DirectoryGraphics(GraphicsController controller, CommandRunner runner) {
        this.controller = controller;
        this.runner = runner;
        load();
        if (homeButton != null)
            homeButton.setOnAction(e -> controller.displayHome());
        if (logoutButton != null)
            logoutButton.setOnAction(e -> controller.displayStartPage());
        if (exitButton != null)
            exitButton.setOnAction(e -> controller.exit());
    }

    protected abstract void config();

    protected abstract FXMLLoader getLoader();

    protected abstract void runCd();

    private void load() {
        FXMLLoader loader = getLoader();
        loader.setController(this);
        try {
                Parent root = loader.load();
                scene = new Scene(root, 1280, 800);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public void display() {
        runCd();
        controller.updatePlayer();
        config();
        controller.setScene(scene);
    }
}
