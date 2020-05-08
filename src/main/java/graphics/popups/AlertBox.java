package graphics.popups;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;

public class AlertBox extends PopupBox {
    @FXML
    private Label label;
    @FXML
    private Button button;

    public AlertBox(String text, String buttonText) {
        load();
        label.setText(text);
        button.setText(buttonText);
        config();
    }

    public AlertBox(String text, Color color, String buttonText) {
        this(text, buttonText);
        label.setTextFill(color);
    }

    protected void configElements() {
        button.setOnAction(e -> close());
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(AlertBox.class.getResource("/fxml/alertBox.fxml"));
    }
}
