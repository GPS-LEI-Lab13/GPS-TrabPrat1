package pt.isec.tests;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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
		//System.out.println(Utils.hashString("random secure password"));
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
		User user = new User("test123user", Utils.hashString("#Dolfin12345"));
		User temp = database.User.getByUsername(user.username);
		if (temp != null) {
			System.out.println(temp);
			deleteUser(temp.id);
		}
		user.id = -1;
		
		assertSame(true, database.User.createUser(user));
		assertNotEquals(-1, user.id);
		assertThrows(MySQLIntegrityConstraintViolationException.class, () -> database.User.createUser(user));
		
		deleteUser(user.id);
	}
	
	void deleteUser(int id) throws SQLException {
		String sql = "delete from user where id = ? ";
		PreparedStatement statement = database.getConnection().prepareStatement(sql);
		statement.setInt(1, id);
		assertSame(1, statement.executeUpdate());
	}
	
	@Test
	void allGetALl() throws SQLException {
		database.User.getAll();
		for (var channel : database.Channel.getAll()) {
			database.Message.getAll(channel.id);
		}
	}
}