package graphics.popups;

import java.io.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

abstract class PopupBox {
    private Stage stage;
    private Parent root;

    protected PopupBox() {
        load();
        config();
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

    protected abstract FXMLLoader getLoader();

    private void config() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
    }

    public void display() {
        stage.showAndWait();
    }

    protected void close() {
        stage.close();
    }
}
