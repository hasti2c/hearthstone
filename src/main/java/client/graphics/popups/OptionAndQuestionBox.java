package client.graphics.popups;

import elements.heros.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import java.util.*;

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
        return new FXMLLoader(OptionAndQuestionBox.class.getResource("/fxml/popups/optionAndQuestionBox.fxml"));
    }

    public boolean getButtonResponse() {
        return buttonResponse;
    }

    public HeroClass getHeroChoice() {
        return HeroClass.valueOf(heroChoice.toUpperCase());
    }

    public String getDeckName() {
        return deckName;
    }
}
