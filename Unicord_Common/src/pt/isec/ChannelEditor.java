package pt.isec;

import java.util.ArrayList;

public class ChannelEditor {
	
	public final int channelId;
	public String name;
	public ArrayList<Integer> usersToAdd;
	public ArrayList<Integer> usersToRemove;
	
	public ChannelEditor(int channelId) {
		this.channelId = channelId;
	}
}
