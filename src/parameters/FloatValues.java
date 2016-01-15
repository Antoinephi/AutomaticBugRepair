package parameters;

public enum FloatValues {

	MIN_FLOAT(Float.MIN_VALUE),
	NEGATIVE (-1.0F),
	ZERO(0.0F),
	POSITIVE(1.0F),
	MAX_FLOAT(Float.MAX_VALUE);
	
	private float val;
	
	FloatValues(float val){
		this.val = val;
	}
	
	public String toString(){
		return this.val + "";
	}
}
