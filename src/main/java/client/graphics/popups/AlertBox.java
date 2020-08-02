package client.graphics.popups;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;

public class AlertBox extends PopupBox {
    @FXML
    private Label label;
    @FXML
    private Button button;

    public AlertBox(String text, String buttonText) {
        label.setText(text);
        button.setText(buttonText);
        button.setOnAction(e -> close());
    }

    public AlertBox(String text, Color color, String buttonText) {
        this(text, buttonText);
        label.setTextFill(color);
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(AlertBox.class.getResource("/fxml/popups/alertBox.fxml"));
    }
}
