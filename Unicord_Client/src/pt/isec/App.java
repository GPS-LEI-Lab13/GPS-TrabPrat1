package pt.isec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import pt.isec.Channel;
import pt.isec.MainReceiver;
import pt.isec.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class App extends Application {
    private Socket socket;
    private ObjectOutputStream oOS;
    private MainReceiver mainReceiver;
    private User user;
    private Channel selectedChannel;
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
        mainReceiver.start();
    }


    public App() { }

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

    public Parent loadFxml(String fxml) throws IOException {
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

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public Stage getStage() {
        return mainStage;
    }

    public Channel getSelectedChannel(){
        return selectedChannel;
    }

    public void setSelectedChannel(Channel selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    public void downloadFile(Message message, String absolutePath) throws IOException, InterruptedException {
        //TODO
        Command command = sendAndReceive(Constants.DOWNLOAD_FILE, message.content);

    }

    public void uploadFile(File file) {
        Message message = new Message(0, user.id, selectedChannel.id, Message.TYPE_FILE, file.getName(),0, user.username);
        message.content = Utils.addTimestampFileName(message.content);
        try {
            Thread td = new Thread(()->{
                try {
                    Command command = sendAndReceive(Constants.NEW_MESSAGE, message);
                    if (command.protocol.equals(Constants.ERROR)){
                        openMessageDialog(Alert.AlertType.ERROR, "Error Dialog", command.extras.toString());
                        return;
                    }else{
                        FileBlock fileBlock = new FileBlock(Constants.UPLOAD_IDENTIFIER + message.content);
                        var bytes = fileBlock.bytes;
                        FileInputStream fIS = new FileInputStream(file);
                        while (true){
                            int readAmount = fIS.read(bytes);
                            if (readAmount <= 0) {
                                fileBlock.bytes = new byte[0];
                                sendCommand(Constants.FILE_BLOCK, fileBlock);
                                fIS.close();
                                break;
                            }
                            if (readAmount < fileBlock.bytes.length) {
                                fileBlock.bytes = Arrays.copyOfRange(bytes, 0, readAmount);
                            }
                            sendCommand(Constants.FILE_BLOCK, fileBlock);
                            fileBlock.bytes = bytes;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            td.setDaemon(true);
            td.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openMessageDialog(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        alert.showAndWait();
    }

    public boolean openMessageDialogDeleteChannel(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        ButtonType buttonTypeDelete = new ButtonType("Delete");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeDelete){
            return true;
        } else {
            return false;
        }
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
