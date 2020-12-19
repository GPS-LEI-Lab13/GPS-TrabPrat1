package pt.isec;


public class Main {
	
	public static void main(String[] args) throws Exception {
		
		if(args.length < 1){
			System.out.println("Invalid arguments: databaseAddress");
		}
		
		String databaseAddress = args[0];
		
		Database db = new Database(
				"jdbc:mysql://" + databaseAddress + ":3306/gps?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&"
						+ "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				"userman",
				"random secure password");

		
	}
}
