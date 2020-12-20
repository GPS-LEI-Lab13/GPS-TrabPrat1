package pt.isec;


public class Main {
	
	public static void main(String[] args) throws Exception {
		
		if(args.length < 1){
			System.out.println("Invalid arguments: databaseAddress");
			System.exit(-1);
		}
		
		String databaseAddress = args[0];
		
		Database database = new Database(
				"jdbc:mysql://" + databaseAddress + ":3306/gps?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&"
						+ "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				"userman",
				"random secure password");

		
		MainServer server = new MainServer(database,Constants.SERVER_PORT);
		server.start();
		
	}
}
