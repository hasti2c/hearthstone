package graphics.directories.collections;

import java.io.*;
import java.util.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import graphics.*;
import graphics.directories.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import javax.swing.text.html.Option;

public abstract class CardsListGraphics extends DirectoryGraphics {
    private Map<String, Boolean> options;
    protected ArrayList<Card> owned = new ArrayList<>(), notOwned = new ArrayList<>();
    protected OptionsGraphics optionsGraphics;
    private SortType sortType = SortType.OWNED_FIRST;
    @FXML
    protected BorderPane border;
    @FXML
    protected HBox topHBox;
    @FXML
    private Button backButton, optionsButton;
    @FXML
    private GridPane grid;
    @FXML
    protected ToggleButton sellModeButton;

    //TODO read options from json
    CardsListGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        backButton.setOnAction(e -> {
            CollectionsGraphics collections = new CollectionsGraphics(controller, runner);
            collections.display();
        });
        optionsButton.setOnAction(e -> optionsGraphics.display());

        options = new HashMap<>(Map.of("Owned", true, "Not Owned", true, "Unlocked", true, "Locked", false, "Minion", true, "Spell", true, "Weapon", true));
    }

    protected void config() {
        clear();
        optionsGraphics.config();

        Player player = controller.getCurrentPlayer();
        owned.addAll(player.getAllCards());
        for (Card c : GameController.getCardsList())
            if (!owned.contains(c))
                notOwned.add(c);
        arrangeCards();
    }

    protected void initTopHBox() {
        optionsGraphics = new OptionsGraphics();
    }

    private void clear() {
        owned = new ArrayList<>();
        notOwned = new ArrayList<>();
        grid.getChildren().clear();
        grid.getRowConstraints().clear();
        grid.getColumnConstraints().clear();
        addRow();
        addRow();
    }

    private void addRow() {
        RowConstraints rc = new RowConstraints(360, 360, 360);
        grid.getRowConstraints().add(rc);
    }

    private void addColumn() {
        ColumnConstraints cc = new ColumnConstraints(240, 240, 240);
        grid.getColumnConstraints().add(cc);
    }

    private void arrangeCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Card c : owned)
            if (options.get("Owned") && selectedCard(c) && validCard(c))
                cards.add(c);
        for (Card c : notOwned)
            if (options.get("Not Owned") && selectedCard(c) && validCard(c))
                cards.add(c);
        cards.sort(this::compare);

        for (int i = 0; i < cards.size(); i++) {
            if (i % 2 == 0)
                addColumn();
            Node n = getNode(cards.get(i));
            grid.add(n, i / 2, i % 2);
            GridPane.setValignment(n, VPos.CENTER);
            GridPane.setHalignment(n, HPos.CENTER);
        }
    }

    private boolean selectedCard(Card card) {
        boolean ret = options.get("Unlocked");
        ret &= options.get(GameController.toProperCase(card.getCardType().toString()));
        ret &= options.get(GameController.toProperCase(card.getHeroClass().toString()));
        if (this instanceof DeckGraphics) {
            if (((DeckGraphics) this).getDeck().getCards().contains(card))
                ret &= options.get("In Deck");
            else
                ret &= options.get("Not In Deck");
        }
        return ret;
    }

    protected abstract boolean validCard(Card card);

    protected abstract Node getNode(Card card);

    protected abstract void runCd();

    protected FXMLLoader getLoader() {
        return new FXMLLoader(CardsListGraphics.class.getResource("/fxml/cardsList.fxml"));
    }

    protected abstract boolean validHero(HeroClass hc);

    private int compare(Card c1, Card c2) {
        return switch (sortType) {
            case OWNED_FIRST: yield 0;
            case ALPHABETICALLY: yield c1.toString().compareTo(c2.toString());
            case BY_MANA: yield c1.getMana() - c2.getMana();
            case BY_RARITY: yield c1.getRarity().getValue() - c2.getRarity().getValue();
            case BY_PRICE: yield c1.getPrice() - c2.getPrice();
        };
    }

    public class OptionsGraphics {
        private Stage stage;
        private Map<String, Boolean> tmpOptions;
        private SortType tmpSortType;
        private ArrayList<CheckBox> checkBoxes;
        @FXML
        private HBox hBox;
        @FXML
        private VBox changeableVBox;
        @FXML
        private ToggleGroup sort;
        @FXML
        private Button doneButton, cancelButton;
        @FXML
        private CheckBox ownedBox, notOwnedBox;

        OptionsGraphics() {
            load();
            clear();
            doneButton.setOnAction(e -> hide(true));
            cancelButton.setOnAction(e -> hide(false));
            initSort();
        }

        protected void config() {
            clear();
            configChangeableVBox();
            configCheckBoxes();
        }

        private void clear() {
            checkBoxes = new ArrayList<>();
            changeableVBox.getChildren().clear();
            tmpOptions = new HashMap<>(options);
            tmpSortType = sortType;
        }

        private void configStage(Scene scene) {
            scene.setOnKeyPressed(e -> {
            if (KeyCode.ENTER.equals(e.getCode()))
                hide(true);
            });
            stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        private void configChangeableVBox() {
            CardsListGraphics parent = CardsListGraphics.this;
            for (HeroClass hc : HeroClass.values())
                addCheckBox(GameController.toProperCase(hc.toString()), parent.validHero(hc), parent.validHero(hc));
            if (parent instanceof DeckGraphics) {
                changeableVBox.getChildren().add(new Separator());
                addCheckBox("In Deck", true, true);
                addCheckBox("Not In Deck", true, true);
            }
        }

        private void addCheckBox(String s, boolean def, boolean enabled) {
            CheckBox cb = new CheckBox(s);
            cb.disableProperty().setValue(!enabled);
            changeableVBox.getChildren().add(cb);
            if (!options.containsKey(s)) {
                options.put(s, def);
                tmpOptions.put(s, def);
            }
        }

        private void configCheckBoxes() {
            for (Node vBox : hBox.getChildren()) {
                assert (vBox instanceof VBox);
                for (Node node : ((VBox) vBox).getChildren())
                    if (node instanceof CheckBox)
                        checkBoxes.add((CheckBox) node);
            }

            for (CheckBox cb : checkBoxes)
                configCheckBox(cb);
        }

        private void configCheckBox(CheckBox checkBox) {
            checkBox.setSelected(tmpOptions.get(checkBox.getText()));
            checkBox.setOnAction(e -> tmpOptions.replace(checkBox.getText(), checkBox.isSelected()));
        }

        private void initSort() {
            sortType = SortType.valueOf(GameController.toEnumCase(((RadioButton) sort.getSelectedToggle()).getText()));

            for (Toggle t : sort.getToggles())
                configToggle((RadioButton) t);
        }

        private void configToggle(RadioButton radioButton) {
            if (sortType.toString().equalsIgnoreCase(radioButton.getText()))
                radioButton.setSelected(true);
            radioButton.setOnAction(e -> {
                if (radioButton.isSelected())
                    tmpSortType = SortType.valueOf(GameController.toEnumCase(radioButton.getText()));
            });
        }

        private void load() {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/options.fxml"));
            loader.setController(this);
            try {
                Parent root = loader.load();
                configStage(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void display() {
            config();
            stage.showAndWait();
        }

        private void hide(boolean save) {
            if (save) {
                options = tmpOptions;
                sortType = tmpSortType;
            }
            stage.hide();
            CardsListGraphics.this.config();
        }

        protected void fixOwned(boolean disable, boolean selected) {
            ownedBox.setSelected(selected);
            ownedBox.setDisable(disable);
            tmpOptions.replace("Owned", selected);
            options.replace("Owned", selected);
        }


        protected void fixNotOwned(boolean disable, boolean selected) {
            notOwnedBox.setSelected(selected);
            notOwnedBox.setDisable(disable);
            tmpOptions.replace("Not Owned", selected);
            options.replace("Not Owned", selected);
        }
    }
}
