package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Login {

    public TextField usernameTextField;
    public TextField passwordTextField;
    public Button registerButton;
    public Button logInButton;

    public void registerButton(ActionEvent actionEvent) {
        try {
            App.getApp().setWindowRoot("Register.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logInButton(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        boolean followRules = Validator.checkUserPasswordRules(password);
        if (followRules){
            User user = new User(username, password);
            try {
                App.getApp().sendCommand(Constants.LOGIN, user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
