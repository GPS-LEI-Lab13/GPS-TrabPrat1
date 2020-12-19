package pt.isec;

import java.io.Serializable;

public class User implements Serializable {
	
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
