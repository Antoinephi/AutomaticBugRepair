package parameters;

public enum BooleanValues {

	TRUE(true),
	FALSE(false);
	
	private boolean val;
	
	BooleanValues(boolean val){
		this.val = val;
	}
	
	public String toString(){
		return this.val + "";
	}
	
}
