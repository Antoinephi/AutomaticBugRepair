package parameters;

public enum ByteValues {

	MIN_BYTE (Byte.MIN_VALUE),
	NEGATIVE ((byte)-1),
	ZERO ((byte)0),
	POSTIVE ((byte)1),
	MAX_BYTE(Byte.MAX_VALUE);
	
	
	private byte val;
	
	ByteValues(byte val){
		this.val = val;
	}
	
	public String toString(){
		return this.val + "";
	}
	
}
