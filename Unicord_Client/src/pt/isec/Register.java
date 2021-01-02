package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Register {

    public TextField usernameTextField;
    public TextField passwordTextField;
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
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();
        App app = App.getApp();

        if (!Validator.checkUsernameRules(username)) {
            app.openMessageDialog(Alert.AlertType.ERROR,Constants.ERROR, "Username need a minimum of 6 characters and a maximum of 25!");
            return;
        }
        if (!Validator.checkUserPasswordRules(password)) {
            app.openMessageDialog(Alert.AlertType.ERROR,Constants.ERROR, "Password need a upper letter, a small letter, a special character and a minimum of 8 characters and a maximum of 25!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            app.openMessageDialog(Alert.AlertType.ERROR, Constants.ERROR, "Password must be the same");
            return;
        }
        try {
            User user = new User(username, Utils.hashString(password));
            Command command = app.sendAndReceive(Constants.REGISTER, user);
            if (!command.protocol.equals(Constants.SUCCESS)) {
                app.openMessageDialog(Alert.AlertType.ERROR, Constants.ERROR, (String) command.extras);
            } else {
                app.openMessageDialog(Alert.AlertType.INFORMATION, "User creation", "User created with success!");
                app.setWindowRoot("Login.fxml");
            }
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
