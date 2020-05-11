package graphics.directories;

import controllers.commands.*;
import graphics.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StartPageGraphics extends DirectoryGraphics {
    @FXML
    private VBox vBox;
    @FXML
    private Button signUpButton, loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label signUpError, loginError;

    public StartPageGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        vBox.getChildren().removeAll(signUpError, loginError);
    }

    @Override
    protected void config() {}

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    @FXML
    private void runSignUp() {
        vBox.getChildren().removeAll(loginError, signUpError);
        if (!runner.run(new Command(CommandType.SIGNUP))) {
            vBox.getChildren().add(0, signUpError);
        } else
            controller.displayHome();
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    private void runLogin() {
        vBox.getChildren().removeAll(loginError, signUpError);
        if (!runner.run(new Command(CommandType.LOGIN))) {
            vBox.getChildren().add(0, loginError);
        } else
            controller.displayHome();
        usernameField.clear();
        passwordField.clear();
    }
    //TODO on action methods in scene builder

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(StartPageGraphics.class.getResource("/fxml/startPage.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.EXIT));
    }
}
