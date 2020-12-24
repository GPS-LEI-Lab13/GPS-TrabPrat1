package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

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
        if (!password.equals(confirmPassword)){
            app.openMessageDialog(Alert.AlertType.ERROR,Constants.ERROR, "Password must be the same");
            return;
        }

        if(Validator.checkUsernameRules(username) && Validator.checkUserPasswordRules(password)){
            User user = new User(username, password);
            try {
                Command command = app.sendAndReceive(Constants.REGISTER, user);
                if (!command.protocol.equals(Constants.SUCCESS)){
                    app.openMessageDialog(Alert.AlertType.ERROR,Constants.ERROR, (String) command.extras);
                }else {
                    app.setWindowRoot("Login.fxml");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            app.openMessageDialog(Alert.AlertType.ERROR,Constants.ERROR, "Password or Username doesn't follow rules");
        }
    }
}
