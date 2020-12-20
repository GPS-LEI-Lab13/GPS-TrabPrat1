package pt.isec;


public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid arguments: server_address, server_UDP_port");
            return;
        }
        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            App app = new App(serverAddress,port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
