package graphics.directories;

import controllers.commands.*;
import graphics.*;
import graphics.directories.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class HomeGraphics extends DirectoryGraphics {
    private PlayGroundGraphics playGround;
    private CollectionsGraphics collections = new CollectionsGraphics(controller, runner);
    private StoreGraphics store = new StoreGraphics(controller, runner);
    private StatsGraphics stats = new StatsGraphics(controller, runner);
    @FXML
    private Button playButton, collectionsButton, storeButton, statsButton, homeLogoutButton, homeExitButton;

    public HomeGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        playButton.setOnAction(e -> displayPlayGround());
        collectionsButton.setOnAction(e -> collections.display());
        storeButton.setOnAction(e -> store.display());
        statsButton.setOnAction(e -> stats.display());
        homeLogoutButton.setOnAction(e -> controller.displayStartPage());
        homeExitButton.setOnAction(e -> controller.exit());
    }

    @Override
    protected void config() {}

    private void displayPlayGround() {
        if (!controller.getCurrentPlayer().getHome().hasPlayGround())
            return;
        playGround = new PlayGroundGraphics(controller, runner);
        playGround.display();
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(HomeGraphics.class.getResource("/fxml/home.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~"));
    }
}
