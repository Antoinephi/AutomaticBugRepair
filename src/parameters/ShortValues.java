package parameters;

public enum ShortValues {

	MIN_SHORT (Short.MIN_VALUE),
	ZERO ((short)0),
	NEGATIVE ((short)-1),
	POSITIVE ((short)1),
	MAX_SHORT (Short.MAX_VALUE);
	
	private short val;
	
	ShortValues(short val){
		this.val = val;
	}
	
	
	public String toString(){
		return this.val +"";
	}
	

	
}
