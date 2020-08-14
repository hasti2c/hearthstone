package client.graphics.popups;

import java.io.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public abstract class PopupBox {
    private Stage stage;
    private Parent root;

    protected PopupBox() {
        load();
        initStage();
    }

    private void load() {
        FXMLLoader loader = getLoader();
        loader.setController(this);
        try {
            this.root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initStage() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
    }

    protected void config() {}

    protected abstract FXMLLoader getLoader();

    public void display() {
        config();
        stage.showAndWait();
    }

    public void close() {
        stage.close();
    }
}
