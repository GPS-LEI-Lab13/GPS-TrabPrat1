package pt.isec;

import java.util.ArrayList;

public class ChannelEditor {
	
	public final int channelId;
	public String name;
	public ArrayList<String> usersIn;
	public ArrayList<String> usersOut;
	
	public ChannelEditor(int channelId) {
		this.channelId = channelId;
	}
	
	@Override
	public String toString() {
		return "ChannelEditor{" +
				"channelId=" + channelId +
				", name='" + name + '\'' +
				", usersToAdd=" + usersIn +
				", usersToRemove=" + usersOut +
				'}';
	}
}
