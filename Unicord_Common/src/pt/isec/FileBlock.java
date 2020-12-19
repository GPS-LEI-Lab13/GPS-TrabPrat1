package pt.isec;

import java.io.Serializable;

public class FileBlock implements Serializable {

	String identifier;
	int offset;
	byte[] bytes;
	
	public FileBlock(String identifier, int offset, byte[] bytes) {
		this.identifier = identifier;
		this.offset = offset;
		this.bytes = bytes;
	}
	
}
