package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Register {

    public TextField usernameTextField;
    public TextField passwordTextFIeld;
    public TextField confirmPasswordTextField;
    public Button cancelButton;
    public Button registerButton;


    public void cancelButton(ActionEvent actionEvent) {
        try {
            App.getApp().setWindowRoot("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerButton(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordTextFIeld.getText();
        String confirmPassword = confirmPasswordTextField.getText();

        if(Validator.checkUsernameRules(username) && Validator.checkUserPasswordRules(password) && password.equals(confirmPassword)){
            User user = new User(username, password);
            try {
                App.getApp().sendCommand(Constants.REGISTER, user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
