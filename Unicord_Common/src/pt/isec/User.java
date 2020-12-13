package pt.isec;

public class User {
	
	public int id;
	public String username;
	public String password;
	
	public User(int id, String username) {
		this.id = id;
		this.username = username;
	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
