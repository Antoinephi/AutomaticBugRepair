package parameters;

public enum IntValues {
	
	MIN_INT (Integer.MIN_VALUE),
	ZERO (0),
	NEGATIVE (-1),
	POSITIVE (1),
	MAX_INT (Integer.MAX_VALUE);
	
	private int val;
	
	IntValues(int val){
		this.val = val;
	}
	
	public int getValue() {
        return val;
    }
	
	public String toString(){
		return this.val +"";
	}
	

}
