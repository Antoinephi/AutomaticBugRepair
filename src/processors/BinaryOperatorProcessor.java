package processors;

import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;

public class BinaryOperatorProcessor extends AbstractProcessor<CtBinaryOperator<?>> {
	public static List<BinaryOperatorKind> binaryOperatorNumber = new ArrayList<>();
	public static List<BinaryOperatorKind> binaryOperatorBoolean = new ArrayList<>();
	static {
		binaryOperatorBoolean.add(BinaryOperatorKind.AND);
		binaryOperatorBoolean.add(BinaryOperatorKind.OR);
		binaryOperatorBoolean.add(BinaryOperatorKind.LT);
		binaryOperatorBoolean.add(BinaryOperatorKind.LE);
		binaryOperatorBoolean.add(BinaryOperatorKind.GT);
		binaryOperatorBoolean.add(BinaryOperatorKind.GE);
		binaryOperatorBoolean.add(BinaryOperatorKind.EQ);
		binaryOperatorBoolean.add(BinaryOperatorKind.NE);
		
		
		binaryOperatorNumber.add(BinaryOperatorKind.DIV);
		binaryOperatorNumber.add(BinaryOperatorKind.PLUS);
		binaryOperatorNumber.add(BinaryOperatorKind.MINUS);
		binaryOperatorNumber.add(BinaryOperatorKind.MOD);
		binaryOperatorNumber.add(BinaryOperatorKind.MUL);
		
	}
	


	@Override
	public void process(CtBinaryOperator<?> binaryOperatorLine) {
		
		if(!(binaryOperatorLine.getClass().getSimpleName().contains("Test")) && !(binaryOperatorLine.getClass().getSimpleName().contains("Obj"))){
			
			if(binaryOperatorBoolean.contains(binaryOperatorLine.getKind())){
				
			}else if(binaryOperatorNumber.contains(binaryOperatorLine.getKind())){
				
			}else{
				return;
			}
			
		}
		
	}

}
