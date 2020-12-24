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
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class App extends Application {
    private Socket socket;
    private ObjectOutputStream oOS;
    private MainReceiver mainReceiver;
    private User user;
    private List<Channel> channels;

    private Stage mainStage;
    private Scene scene;
    public static String serverAddress;
    private static App instance;

    public static App getApp() {
        return instance;
    }

    public void initialize() throws IOException {
        socket = new Socket(serverAddress, Constants.SERVER_PORT);
        oOS = new ObjectOutputStream(socket.getOutputStream());
        mainReceiver = new MainReceiver(socket);
    }


    public App() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        instance = this;
        mainStage = primaryStage;

        initialize();
        System.out.println("Connection Successful");

        primaryStage.setTitle("Unicord");
        Parent root = loadFxml("fxml/Login.fxml");
        scene = new Scene(root, 600, 460);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid arguments: server_address");
            return;
        }
        String serverAddress = args[0];
        try {
            System.out.println("Trying to connect");
            App.serverAddress = serverAddress;
            launch();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Parent loadFxml(String fxml) throws IOException {
        return FXMLLoader.load(getClass().getResource(fxml));
    }

    public void setWindowRoot(String fxml) throws IOException {
        scene.setRoot(loadFxml("fxml/" + fxml));
    }

    public BlockingQueue<Command> getReceivedObjectQueue() {
        //TODO NEED CLERIFICATION HOW TO DO THIS
        return mainReceiver.addListener();
    }

    public void sendCommand(String protocol, Object obj) throws IOException {
        Command command = new Command(protocol, obj);
        System.out.println("Sent: " + command);
        oOS.writeUnshared(command);
    }
    public Command sendAndReceive(String protocol, Object obj) throws IOException, InterruptedException {
        BlockingQueue<Command> commands = mainReceiver.addListener();
        sendCommand(protocol,obj);
        while (true){
            Command command = commands.take();
            System.out.println("Receive: " + command);
            if (command.protocol.equals(Constants.SUCCESS) || command.protocol.equals(Constants.ERROR)){
                mainReceiver.removeListener(commands);
                return command;
            }
        }

    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void downloadFile() {

    }

    public void uploadFile() {

    }

    public void openMessageDialog(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        alert.showAndWait();
    }

    public User getUser() {
        return this.user;
    }
}
