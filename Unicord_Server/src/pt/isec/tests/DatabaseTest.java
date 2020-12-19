package pt.isec.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isec.Database;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
	
	int userId;
	int channelId;
	Database database;
	String databaseAddress = "localhost";
	
	@BeforeEach
	void setUp() throws SQLException {
		database = new Database("jdbc:mysql://" + databaseAddress + ":3306/gps?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&"
				+ "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				"userman",
				"random secure password");
	}
	
	@AfterEach
	void tearDown() {
	}
	
	@Test
	void getChannels(){
		//database.Channel.createChannel()
	}
	
	@Test
	void getMessages(){
	
	}
	
	@Test
	void getUsers(){
	
	}
}