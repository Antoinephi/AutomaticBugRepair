package parameters;

public enum LongValues {

	MIN_LONG (Long.MIN_VALUE),
	NEGATIVE (-1L),
	ZERO (0L),
	POSTIVE (1L),
	MAX_LONG(Long.MAX_VALUE);
	
	
	private long val;
	
	LongValues(long val){
		this.val = val;
	}
	
	public String toString(){
		return this.val + "";
	}
}
