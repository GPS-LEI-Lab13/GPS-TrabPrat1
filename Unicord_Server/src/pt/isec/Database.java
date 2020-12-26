package pt.isec;

import java.sql.*;
import java.util.ArrayList;

public class Database {
	
	private final Connection connection;
	public final Messages Message;
	public final Channels Channel;
	public final Users User;
	
	public Database(String host, String user, String password) throws SQLException {
		connection = DriverManager.getConnection(host, user, password);
		Message = new Messages();
		Channel = new Channels();
		User = new Users();
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public class Messages {
		public ArrayList<Message> getAll(int channelId) throws SQLException {
			String sql = "select id,sender_id,channel_id,moment_sent,type,content, " +
					"(select username from user where sender_id = id) as sender_username " +
					"from message " +
					"where channel_id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, channelId);
			return parse(statement.executeQuery());
		}
		
		private ArrayList<Message> parse(ResultSet result) throws SQLException {
			ArrayList<Message> list = new ArrayList<>();
			while (result.next()) {
				list.add(new Message(result.getInt("id"),
						result.getInt("sender_id"),
						result.getInt("channel_id"),
						result.getString("type"),
						result.getString("content"),
						result.getTimestamp("moment_sent").getTime(),
						result.getString("sender_username")));
			}
			return list;
		}
		
		public boolean createMessage(Message message) throws SQLException {
			String sql = "insert into message(id, sender_id, channel_id, type, content) values(?, ?, ?, ?, ?) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			message.id = getLastID() + 1;
			statement.setInt(1, message.id);
			statement.setInt(2, message.senderId);
			statement.setInt(3, message.channelId);
			statement.setString(4, message.type);
			statement.setString(5, message.content);
			return statement.executeUpdate() == 1;
		}
		
		public Message getByID(int id) throws SQLException {
			String sql = "select id,sender_id,channel_id,moment_sent,type,content, " +
					"(select username from user where sender_id = id) as sender_username " +
					"from message where id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ArrayList<Message> list = parse(statement.executeQuery());
			return list.size() == 0 ? null : list.get(0);
		}
		
		private int getLastID() throws SQLException {
			String sql = "select max(id) as id " +
					"from message ";
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();
			if (!result.next()) return -1;
			return result.getInt(1);
		}
	}
	
	public class Channels {
		public ArrayList<Channel> getAll() throws SQLException {
			String sql = "select id,creator_id,name " +
					"from channel ";
			PreparedStatement statement = connection.prepareStatement(sql);
			return parse(statement.executeQuery());
		}
		
		private ArrayList<Channel> parse(ResultSet result) throws SQLException {
			ArrayList<Channel> list = new ArrayList<>();
			while (result.next()) {
				list.add(new Channel(result.getInt("id"),
						result.getInt("creator_id"),
						result.getString("name")));
			}
			return list;
		}
		
		public ArrayList<Channel> getUserChannels(int userId) throws SQLException {
			String sql = "select * from (\n" +
					"\t(select id, creator_id, name \n" +
					"\tfrom channel, channel_user \n" +
					"\twhere channel_id = id and user_id = ? )\n" +
					"    union\n" +
					"    (select id, creator_id, name \n" +
					"\tfrom channel \n" +
					"\twhere creator_id = ?)\n" +
					") collection;";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, userId);
			statement.setInt(2, userId);
			return parse(statement.executeQuery());
		}
		
		public boolean createChannel(Channel channel) throws SQLException {
			String sql = "insert into channel(id, creator_id, name) values(?, ?, ?) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			int tempId = getLastID() + 1;
			statement.setInt(1, tempId);
			statement.setInt(2, channel.creatorId);
			statement.setString(3, channel.name);
			boolean changeIt = statement.executeUpdate() == 1;
			if (changeIt) {
				channel.id = tempId;
			}
			return changeIt;
		}
		
		public boolean editChannel(Channel channel) throws SQLException {
			String sql = "update channel set name = ? where id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, channel.name);
			statement.setInt(2, channel.id);
			return statement.executeUpdate() == 1;
		}
		
		public boolean deleteChannel(int channelId) throws SQLException {
			String sql = "delete from channel where id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, channelId);
			return statement.executeUpdate() == 1;
		}
		
		public boolean addUser(int userId, int channelId) throws SQLException {
			String sql = "insert into channel_user(channel_id, user_id) values(?, ?) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, channelId);
			statement.setInt(2, userId);
			return statement.executeUpdate() == 1;
		}
		
		public boolean removeUser(int userId, int channelId) throws SQLException {
			String sql = "delete from channel_user where channel_id = ? and user_id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, channelId);
			statement.setInt(2, userId);
			return statement.executeUpdate() == 1;
		}
		
		public Channel getByID(int id) throws SQLException {
			String sql = "select id,creator_id,name from channel where id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ArrayList<Channel> list = parse(statement.executeQuery());
			return list.size() == 0 ? null : list.get(0);
		}
		
		public Channel getByName(String name) throws SQLException {
			String sql = "select id, creator_id, name from channel where name = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			ArrayList<Channel> list = parse(statement.executeQuery());
			return list.size() == 0 ? null : list.get(0);
		}
		
		private int getLastID() throws SQLException {
			String select = "select max(id) as id from channel ";
			PreparedStatement statement = connection.prepareStatement(select);
			ResultSet result = statement.executeQuery();
			if (!result.next()) return -1;
			return result.getInt(1);
		}
		
	}
	
	public class Users {
		public ArrayList<User> getAll() throws SQLException {
			String sql = "select id, username from user ";
			PreparedStatement statement = connection.prepareStatement(sql);
			return parse(statement.executeQuery());
		}
		
		public ArrayList<User> parse(ResultSet result) throws SQLException {
			ArrayList<User> list = new ArrayList<>();
			while (result.next()) {
				list.add(new User(result.getInt("id"), result.getString("username")));
			}
			return list;
		}
		
		public boolean createUser(User user) throws SQLException {
			String sql = "insert into user(id, username, password_hash) values(?, ?, ?) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			int tempId = getLastID() + 1;
			statement.setInt(1, tempId);
			statement.setString(2, user.username);
			statement.setString(3, user.password);
			boolean success = statement.executeUpdate() == 1;
			if (success) {
				user.id = tempId;
			}
			return success;
		}
		
		public ArrayList<User> getLike(String str) throws SQLException {
			String sql = "select id, username from user where username like ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, "%" + str + "%");
			return parse(statement.executeQuery());
		}
		
		public User getByID(int id) throws SQLException {
			String sql = "select id, username from user where id = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ArrayList<User> list = parse(statement.executeQuery());
			return list.size() == 0 ? null : list.get(0);
		}
		
		public User getByUsername(String name) throws SQLException {
			String sql = "select id, username from user where username = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			ArrayList<User> list = parse(statement.executeQuery());
			return list.size() == 0 ? null : list.get(0);
		}
		
		public int getLastID() throws SQLException {
			String sql = "select max(id) as id from user ";
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();
			if (!result.next()) return -1;
			return result.getInt(1);
		}
		
		public boolean doesPasswordMatchUsername(String username, String password) throws SQLException {
			String sql = "select password_hash from user where username = ? ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if (!result.next()) return false;
			return result.getString(1).equals(password);
		}
	}
}
