package client.graphics.directories;

import java.io.*;
import client.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;

public abstract class Directory {
    protected Client client;
    protected ClientController controller;
    protected Scene scene;
    @FXML
    protected Button homeButton;
    @FXML
    protected MenuItem logoutButton, exitButton;

    protected Directory(ClientController controller, Client client) {
        this.controller = controller;
        this.client = client;
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
        //controller.updatePlayer();
        config();
        controller.setScene(scene);
    }
}
