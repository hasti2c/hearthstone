package client.graphics.directories;

import client.*;
import commands.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import static commands.types.ServerCommandType.*;

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

    public StartPage(ClientController controller, Client client) {
        super(controller, client);
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
        client.request(new Command<>(SIGN_UP, usernameField.getText(), passwordField.getText()));
        usernameField.clear();
        passwordField.clear();
    }

    public void signUpResult(boolean success) {
        if (success)
            controller.displayHome();
        else
            vBox.getChildren().add(0, signUpError);
    }

    @FXML
    private void runLogin() {
        vBox.getChildren().removeAll(loginError, signUpError);
        client.request(new Command<>(LOGIN, usernameField.getText(), passwordField.getText()));
        usernameField.clear();
        passwordField.clear();
    }

    public void loginResult(boolean success) {
        if (success)
            controller.displayHome();
        else
            vBox.getChildren().add(0, loginError);
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(StartPage.class.getResource("/fxml/directories/startPage.fxml"));
    }
}
