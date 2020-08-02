package client.graphics.popups;

import javafx.fxml.*;
import javafx.scene.control.*;
import java.util.*;

public class OptionBox extends PopupBox {
    private String choice;
    private boolean buttonResponse;
    @FXML
    private Label label;
    @FXML
    private Button yesButton, noButton;
    @FXML
    private ChoiceBox<String> choiceBox;

    public OptionBox(String text, String yesText, String noText, ArrayList<String> choices) {
        label.setText(text);
        yesButton.setText(yesText);
        noButton.setText(noText);
        choiceBox.getItems().addAll(choices);

        yesButton.setOnAction(e -> {
            buttonResponse = true;
            choice = choiceBox.getValue();
            if (choice != null)
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

    public String getChoice() {
        return choice;
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(OptionBox.class.getResource("/fxml/popups/optionBox.fxml"));
    }
}
