package pt.isec;

import java.io.Serializable;

public class Channel implements Serializable {
	
	public int id;
	public int creatorId;
	public String name;
	
	public Channel(int creatorId, String name) {
		this.creatorId = creatorId;
		this.name = name;
	}
	
	public Channel(int id, int creatorId, String name) {
		this.id = id;
		this.creatorId = creatorId;
		this.name = name;
	}
}
