package pt.isec;


public class Main {
	
	public static void main(String[] args) throws Exception {
		
		String databaseAddress = "localhost";
		
		Database db = new Database(
				"jdbc:mysql://" + databaseAddress + ":3306/gps?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&"
						+ "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				"userman",
				"random secure password");

		
	}
}
