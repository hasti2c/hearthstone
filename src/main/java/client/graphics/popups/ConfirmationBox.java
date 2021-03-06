package client.graphics.popups;

import javafx.fxml.*;
import javafx.scene.control.*;

public class ConfirmationBox extends PopupBox {
    private boolean response;
    @FXML
    private Label label;
    @FXML
    private Button yesButton, noButton;

    public ConfirmationBox(String text, String yesText, String noText) {
        label.setText(text);
        yesButton.setText(yesText);
        noButton.setText(noText);yesButton.setOnAction(e -> {
            response = true;
            close();
        });
        noButton.setOnAction(e -> {
            response = false;
            close();
        });
    }

    public boolean getResponse() {
        return response;
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(ConfirmationBox.class.getResource("/fxml/popups/confirmationBox.fxml"));
    }
}
