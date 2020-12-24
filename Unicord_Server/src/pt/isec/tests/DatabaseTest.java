package pt.isec.tests;

import org.junit.jupiter.api.Test;
import pt.isec.Constants;
import pt.isec.Database;
import pt.isec.User;
import pt.isec.Utils;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
	
	Database database;
	
	public DatabaseTest() throws NoSuchAlgorithmException, SQLException {
		System.out.println(Utils.hashString("random secure password"));
		this.database = new Database(Constants.getDatabaseConnectionString(""),
				Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
	}
	
	@Test
	void checkGetByUsername() throws Exception {
		assertSame(true, database.User.getByUsername("Admin") != null);
		assertSame(false, database.User.getByUsername("teskdieowmcowei") != null);
	}
	
	@Test
	void checkGetLike() throws Exception {
		assertSame(1, database.User.getLike("Admin").size());
		assertSame(1, database.User.getLike("dmin").size());
		assertSame(0, database.User.getLike("dmindsadsadwq").size());
	}
	
	@Test
	void checkCreateUser() throws Exception {
		User user = new User("test123user", Utils.hashString("badpass"));
		if(database.User.getByUsername("test123user") == null){
			String sql = "delete from user where id = ? ";
			PreparedStatement statement = database.getConnection().prepareStatement(sql);
			statement.setInt(1, user.id);
			assertSame(1, statement.executeUpdate());
		}
		user.id = -1;
		assertSame(false, database.User.createUser(user));
		user.password = Utils.hashString("#Dolfin12345");
		assertSame(true, database.User.createUser(user));
		assertNotEquals(-1, user.id);
		assertSame(false, database.User.createUser(user));
		
	}
	
	
}