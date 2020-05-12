package graphics.popups;

import javafx.fxml.*;
import javafx.scene.control.*;

public class QuestionBox extends PopupBox {
    private boolean buttonResponse;
    private String text;
    @FXML
    private Label label;
    @FXML
    private TextField textField;
    @FXML
    private Button yesButton, noButton;

    public QuestionBox(String text, String yesText, String noText) {
        label.setText(text);
        yesButton.setText(yesText);
        noButton.setText(noText);
        yesButton.setOnAction(e -> {
            this.text = textField.getText();
            buttonResponse = true;
            close();
        });
        noButton.setOnAction(e -> {
            buttonResponse = false;
            close();
        });
    }

    public boolean getButtonResponse() {
        return buttonResponse;
    }

    public String getText() {
        return text;
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(QuestionBox.class.getResource("/fxml/popups/questionBox.fxml"));
    }
}
