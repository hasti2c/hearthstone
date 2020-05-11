package graphics.popups;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class OptionAndQuestionBox extends PopupBox {
    private String heroChoice, deckName;
    private boolean buttonResponse;
    @FXML
    private Label label;
    @FXML
    private Button yesButton, noButton;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField textField;

    public OptionAndQuestionBox(String text, String yesText, String noText, ArrayList<String> choices) {
        label.setText(text);
        yesButton.setText(yesText);
        noButton.setText(noText);
        choiceBox.getItems().addAll(choices);
        yesButton.setOnAction(e -> {
           buttonResponse = true;
           heroChoice = choiceBox.getValue();
           deckName = textField.getText();
           if (heroChoice != null)
               close();
        });
        noButton.setOnAction(e -> {
            buttonResponse = false;
            close();
        });
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(OptionAndQuestionBox.class.getResource("/fxml/optionAndQuestionBox.fxml"));
    }

    public boolean getButtonResponse() {
        return buttonResponse;
    }

    public String getHeroChoice() {
        return heroChoice;
    }

    public String getDeckName() {
        return deckName;
    }
}
