package graphics.directories;

import controllers.commands.*;
import graphics.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StartPage extends Directory {
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

    public StartPage(GraphicsController controller, CommandRunner runner) {
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
        if (!runner.run(new Command(CommandType.SIGN_UP))) {
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

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(StartPage.class.getResource("/fxml/directories/startPage.fxml"));
    }
}
