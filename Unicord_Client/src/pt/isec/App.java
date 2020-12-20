package pt.isec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import pt.isec.Channel;
import pt.isec.MainReceiver;
import pt.isec.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class App extends Application {
    private Socket socket;
    private final MainReceiver mainReceiver;
    private User user;
    private List<Channel> channels;

    private Stage mainStage;
    private Scene scene;

    public App(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress,port);
        mainReceiver = new MainReceiver(socket);
        launch(null);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        mainStage = primaryStage;
        primaryStage.setTitle("Unicord");
        Parent root = loadFxml("Login.sample.fxml");
        scene = new Scene(root, 600, 460);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Parent loadFxml(String fxml) throws IOException {
        return FXMLLoader.load(getClass().getResource(fxml));
    }

    public void setWindowRoot(String fxml) throws IOException {
        scene.setRoot(loadFxml(fxml));
    }
    public BlockingQueue<Command> getReceivedObjectQueue(){
        //TODO NEED CLERIFICATION HOW TO DO THIS
        return mainReceiver.addListener();
    }
    void sendCommand(String protocol,Object obj){

    }

    public List<Channel> getChannels() {
        return channels;
    }

    void downloadFile(){

    }

    void uploadFile(){

    }

    void openMessageDialog(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        alert.showAndWait();
    }
}
