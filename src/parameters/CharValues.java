package parameters;

public enum CharValues {

	MIN_CHAR (Character.MIN_VALUE),
	NEGATIVE ((char)-1),
	ZERO ((char)0),
	POSTIVE ((char)1),
	MAX_CHAR(Character.MAX_VALUE);
		
	private char val;
	
	CharValues(char val){
		this.val = val;
	}
	
	public String toString(){
		return this.val + "";
	}
	
}
