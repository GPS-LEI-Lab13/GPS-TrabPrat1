package pt.isec;

import java.io.Serial;
import java.io.Serializable;

public class FileBlock implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 50348927L;
	
	String identifier;
	int offset;
	byte[] bytes;
	
	public FileBlock(String identifier, int offset, byte[] bytes) {
		this.identifier = identifier;
		this.offset = offset;
		this.bytes = bytes;
	}
	
}
