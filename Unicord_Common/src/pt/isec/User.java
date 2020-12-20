package pt.isec;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 12346789L;
	
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
	
	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
