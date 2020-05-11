package graphics.popups;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.ArrayList;

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
        return new FXMLLoader(OptionBox.class.getResource("/fxml/optionBox.fxml"));
    }
}
