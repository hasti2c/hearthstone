package graphics.directories.collections;

import java.util.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.Player.Player;
import gameObjects.cards.*;
import gameObjects.heros.*;
import graphics.*;
import graphics.directories.*;
import graphics.popups.PopupBox;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;

public abstract class CardsList extends Directory {
    private Map<String, Boolean> options;
    protected ArrayList<Card> owned = new ArrayList<>(), notOwned = new ArrayList<>();
    protected OptionsPage optionsPage;
    private SearchPage searchPage;
    private SortType sortType = SortType.OWNED_FIRST;
    private String searchText = "";
    private Pair<Integer, Integer> manaRange, healthRange, attackRange;
    @FXML
    protected BorderPane border;
    @FXML
    private Button backButton, searchButton;
    @FXML
    private MenuItem optionsButton, advancedSearchButton;
    @FXML
    private GridPane grid;
    @FXML
    private TextField searchField;

    CardsList(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        if (backButton != null)
            backButton.setOnAction(e -> {
            Collections collections = new Collections(controller, runner);
            collections.display();
        });
        optionsButton.setOnAction(e -> optionsPage.display());
        searchButton.setOnAction(e -> {
            searchText = searchField.getText();
            config();
        });
        advancedSearchButton.setOnAction(e -> searchPage.display());

        options = new HashMap<>(Map.of("Owned", true, "Not Owned", true, "Unlocked", true, "Locked", false, "Minion", true, "Spell", true, "Weapon", true));
        manaRange = new Pair<>(null, null);
        healthRange = new Pair<>(null, null);
        attackRange = new Pair<>(null, null);
    }

    protected void config() {
        clear();
        optionsPage.config();

        Player player = controller.getCurrentPlayer();
        owned.addAll(player.getInventory().getAllCards());
        for (Card c : GameController.getCardsList())
            if (!owned.contains(c))
                notOwned.add(c);
        arrangeCards();
    }

    protected void initTopHBox() {
        optionsPage = new OptionsPage();
        searchPage = new SearchPage();
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
            if (options.get("Owned") && shouldShow(c))
                cards.add(c);
        for (Card c : notOwned)
            if (options.get("Not Owned") && shouldShow(c))
                cards.add(c);
        cards.sort(this::compare);

        for (int i = 0; i < cards.size(); i++) {
            if (i % 2 == 0)
                addColumn();
            Node n = getNode(cards.get(i));
            grid.add(n, i / 2, i % 2);
            GridPane.setValignment(n, VPos.TOP);
            GridPane.setHalignment(n, HPos.CENTER);
        }
    }

    private boolean shouldShow(Card card) {
        return validCard(card) && selectedCard(card) && searchedCard(card) && advancedSearchedCard(card);
    }

    protected abstract boolean validCard(Card card);

    private boolean selectedCard(Card card) {
        boolean ret = options.get("Unlocked");
        ret &= options.get(GameController.toProperCase(card.getCardType().toString()));
        ret &= options.get(GameController.toProperCase(card.getHeroClass().toString()));
        if (this instanceof Deck dg) {
            if (dg.getDeck().getCards().contains(card))
                ret &= options.get("In Deck");
            else
                ret &= options.get("Not In Deck");
        }
        return ret;
    }

    private boolean searchedCard(Card card) {
        if (searchText == null || "".equals(searchText))
            return true;
        String cardName = card.toString();
        for (int i = 1; i <= cardName.length(); i++)
            if (searchText.equalsIgnoreCase(cardName.substring(0, i)))
                return true;
        return false;
    }

    private boolean advancedSearchedCard(Card card) {
        boolean ret = inRange(card.getMana(), manaRange);
        if (card instanceof Minion m) {
            ret &= inRange(m.getHP(), healthRange);
            ret &= inRange(m.getAttack(), attackRange);
        } else if (card instanceof Weapon w) {
            ret &= inRange(w.getDurability(), healthRange);
            ret &= inRange(w.getAttack(), attackRange);
        }
        return ret;
    }

    private boolean inRange(int n, Pair<Integer, Integer> range) {
        return (range.getKey() == null || range.getKey() <= n) && (range.getValue() == null || n <= range.getValue());
    }

    protected abstract Node getNode(Card card);

    protected abstract boolean validHero(HeroClass hc);

    @Override
    public void display() {
        searchText = null;
        super.display();
    }

    public void search(String searchText) {
        searchField.setText(searchText);
        this.searchText = searchText;
        config();
    }

    private int compare(Card c1, Card c2) {
        return switch (sortType) {
            case OWNED_FIRST: yield 0;
            case ALPHABETICALLY: yield c1.toString().compareTo(c2.toString());
            case BY_MANA: yield c1.getMana() - c2.getMana();
            case BY_RARITY: yield c1.getRarity().getValue() - c2.getRarity().getValue();
            case BY_PRICE: yield c1.getPrice() - c2.getPrice();
        };
    }

    public class OptionsPage extends PopupBox {
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

        OptionsPage() {
            doneButton.setOnAction(e -> close(true));
            cancelButton.setOnAction(e -> close(false));
            initSort();
            config();
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

        private void configChangeableVBox() {
            CardsList parent = CardsList.this;
            for (HeroClass hc : HeroClass.values())
                addCheckBox(GameController.toProperCase(hc.toString()), parent.validHero(hc), parent.validHero(hc));
            if (parent instanceof Deck) {
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
                    if (node instanceof CheckBox cb)
                        checkBoxes.add(cb);
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

        @Override
        protected FXMLLoader getLoader() {
            return new FXMLLoader(this.getClass().getResource("/fxml/popups/options.fxml"));
        }

        private void close(boolean save) {
            if (save) {
                options = tmpOptions;
                sortType = tmpSortType;
            }
            super.close();
            CardsList.this.config();
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

    public class SearchPage extends PopupBox {
        @FXML
        private Button doneButton, cancelButton, clearMana, clearHealth, clearAttack;
        @FXML
        private TextField manaMin, manaMax, healthMin, healthMax, attackMin, attackMax;

        SearchPage() {
            doneButton.setOnAction(e -> close(true));
            cancelButton.setOnAction(e -> close(false));

            clearMana.setOnAction(e -> {
                manaMin.setText(null);
                manaMax.setText(null);
            });
            clearHealth.setOnAction(e -> {
                healthMin.setText(null);
                healthMax.setText(null);
            });
            clearAttack.setOnAction(e -> {
                attackMin.setText(null);
                attackMax.setText(null);
            });
        }

        @Override
        protected void config() {
            if (manaRange.getKey() != null)
            manaMin.setText(manaRange.getKey() + "");
            if (manaRange.getValue() != null)
            manaMax.setText(manaRange.getValue() + "");
            if (healthRange.getKey() != null)
            healthMin.setText(healthRange.getKey() + "");
            if (healthRange.getValue() != null)
            healthMax.setText(healthRange.getValue() + "");
            if (attackRange.getKey() != null)
            attackMin.setText(attackRange.getKey() + "");
            if (attackRange.getValue() != null)
            attackMax.setText(attackRange.getValue() + "");
        }

        private void close(boolean save) {
            if (save) {
                manaRange = new Pair<>(getInt(manaMin), getInt(manaMax));
                healthRange = new Pair<>(getInt(healthMin), getInt(healthMax));
                attackRange = new Pair<>(getInt(attackMin), getInt(attackMax));
            }
            super.close();
            CardsList.this.config();
        }

        private Integer getInt(TextField field) {
            try {
                return Integer.parseInt(field.getText());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        protected FXMLLoader getLoader() {
            return new FXMLLoader(SearchPage.class.getResource("/fxml/popups/search.fxml"));
        }
    }
}
