package pt.isec;

public class Message {
	
	public int id;
	public int senderId;
	public int channelId;
	public long date;
	public String type;
	public String content;
	public String senderUsername;
	
	public Message(int senderId, int channelId, String type, String content) {
		this.senderId = senderId;
		this.channelId = channelId;
		this.type = type;
		this.content = content;
	}
	
	public Message(int id, int senderId, int channelId, String type, String content, long date, String senderUsername) {
		this.id = id;
		this.senderId = senderId;
		this.channelId = channelId;
		this.type = type;
		this.content = content;
		this.date = date;
		this.senderUsername = senderUsername;
	}
}
