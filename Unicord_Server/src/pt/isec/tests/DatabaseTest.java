package pt.isec.tests;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;
import pt.isec.*;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

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
	
	@Test
	void getUserChannels() throws SQLException, NoSuchAlgorithmException {
		User user = new User("testUser321890312", Utils.hashString("testPass"));
		User temp = database.User.getByUsername(user.username);
		if (temp != null) {
			deleteUser(temp.id);
		}
		assertSame(true, database.User.createUser(user));
		Channel channel = database.Channel.getAll().get(0);
		
		assertSame(true, database.Channel.addUser(user.id, channel.id));
		ArrayList<Channel> userChannels = database.Channel.getUserChannels(user.id);
		assertSame(1, userChannels.size());
		assertSame(true, database.Channel.removeUser(user.id, channel.id));
		userChannels = database.Channel.getUserChannels(user.id);
		assertSame(0, userChannels.size());
		
		deleteUser(user.id);
	}
	
	@Test
	void createNRemoveChannel() throws SQLException {
		User admin = database.User.getByUsername("Admin");
		Channel channel = new Channel(admin.id, "testChannelDatabaseTests4910");
		Channel temp = database.Channel.getByName(channel.name);
		if (temp != null) {
			assertSame(true, database.Channel.deleteChannel(temp.id));
		}
		assertSame(true, database.Channel.createChannel(channel));
		boolean didCreateIt = false;
		for (Channel cha : database.Channel.getUserChannels(admin.id)) {
			if (cha.name.equals(channel.name)) {
				didCreateIt = true;
				break;
			}
		}
		assertSame(true, didCreateIt);
		assertSame(true, database.Channel.deleteChannel(channel.id));
	}
	
	@Test
	void editNDeleteChannel() throws SQLException {
		Channel channel = new Channel(1, "testCtabaseTests31298");
		assertSame(true, database.Channel.createChannel(channel));
		channel.name = "newName";
		assertSame(true, database.Channel.editChannel(channel));
		Channel changed = database.Channel.getByID(channel.id);
		assertSame(true, changed != null);
		assertSame(true, changed.name.equals(channel.name));
		
		assertSame(true, database.Channel.deleteChannel(channel.id));
	}
}