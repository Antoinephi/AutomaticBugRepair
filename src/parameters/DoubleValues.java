package parameters;

public enum DoubleValues {

	MIN_DOUBLE (Double.MIN_VALUE),
	NEGATIVE (-1.0D),
	ZERO (0.0D),
	POSITIVE (1.0D),
	MAX_DOUBLE(Double.MAX_VALUE);
	
	private double val;
	
	DoubleValues(double val){
		this.val = val;
	}
	
	public String toString(){
		return this.val  + "";
	}
	
}
