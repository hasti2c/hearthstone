package client.graphics.popups;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class QuestionBox extends PopupBox {
    private boolean buttonResponse;
    private String text;
    private final boolean password;
    @FXML
    private VBox vBox;
    @FXML
    private Label label;
    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button yesButton, noButton;

    public QuestionBox(String text, String yesText, String noText, boolean password) {
        this.password = password;
        if (password)
            vBox.getChildren().remove(textField);
        else
            vBox.getChildren().remove(passwordField);

        label.setText(text);
        yesButton.setText(yesText);
        noButton.setText(noText);
        yesButton.setOnAction(e -> {
            if (!this.password)
                this.text = textField.getText();
            else
                this.text = passwordField.getText();
            buttonResponse = true;
            close();
        });
        noButton.setOnAction(e -> {
            buttonResponse = false;
            close();
        });
    }

    public QuestionBox(String text, String yesText, String noText) {
        this(text, yesText, noText, false);
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
