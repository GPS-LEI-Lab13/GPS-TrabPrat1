package pt.isec;

import java.io.File;

public class Constants {
	
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String REGISTER = "REGISTER";
	public static final String LOGIN = "LOGIN";
	public static final String LOGOUT = "LOGOUT";
	public static final String GET_CHANNELS = "GET_CHANNELS";
	public static final String GET_MESSAGES = "GET_MESSAGES";
	public static final String NEW_MESSAGE = "NEW_MESSAGE";
	public static final String DOWNLOAD_MESSAGES = "DOWNLOAD_MESSAGE";
	public static final String GET_USERS = "GET_USERS";
	public static final String NEW_CHANNEL = "NEW_CHANNEL";
	public static final String EDIT_CHANNEL = "EDIT_CHANNEL";
	public static final String FILE_BLOCK = "FILE_BLOCK";
	
	public static final String ADD_CHANNEL_USER = "ADD_CHANNEL_USER";
	public static final String REMOVE_CHANNEL_USER = "REMOVE_CHANNEL_USER";
	public static final String EDIT_CHANNEL_NAME = "EDIT_CHANNEL_NAME";
	
	public static final String SERVER_SHUTDOWN = "SERVER_SHUTDOWN";
	
	
	public static File getFile(String fileName) {
		File file = new File("files" + File.separator + fileName);
		Utils.createFileDirectories(file);
		return file;
	}
}
