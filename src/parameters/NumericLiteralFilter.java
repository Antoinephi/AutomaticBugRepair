package parameters;

import java.util.LinkedList;
import java.util.List;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.visitor.Filter;

public class NumericLiteralFilter implements Filter<CtLiteral<Number>>{

	private static List<String> numericTypes = new LinkedList<String>();
	
	static {
		numericTypes.add("int");
		numericTypes.add("Integer");
		numericTypes.add("double");
		numericTypes.add("Double");
		numericTypes.add("float");
		numericTypes.add("Float");
		numericTypes.add("short");
		numericTypes.add("Short");
	}
	
	@Override
	public boolean matches(CtLiteral<Number> l) {
		if(numericTypes.contains(l.getType().getSimpleName()))
			return true;
		return false;
	}

}
