package processors;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import parameters.IntValues;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class IntMutatorProcessor extends AbstractProcessor<CtClass<?>> {
	private List<CtLiteral<?>> variables = new LinkedList<CtLiteral<?>>();
	private Random r = new Random();
	private int i = 0;


	public void init(){
//		System.out.println(this.getEnvironment().toString());
	}
	
	public void process(CtClass<?> c) {			
		if(!c.getSimpleName().contains("Test") && !c.getSimpleName().contains("Obj")){
			variables.addAll(Query.getElements(c, new TypeFilter<CtLiteral<Integer>>(CtLiteral.class)));
			//			System.out.println("Class n°" + i);
//			System.out.println("Variable : " + returnRandomVariable(variables).getSignature() + " => " + returnRandomValue());
			returnMutation();
			/*for(CtLiteral<?> v :  variables){
				if(v.getType().getSimpleName().equals("int"))
					System.out.println(variables.indexOf(v) + " : " + v.getSignature());
			}*/
		}
	}
	
	public void returnMutation(){
		//CtLiteral<Integer> toMutateVal = (CtLiteral<Integer>) returnRandomVariable(variables);
		CtLiteral<Integer> toMutateVal = null;
		for(int j = i; j < variables.size(); j++){
			System.out.println(variables.get(j));
			if(variables.get(j).getType().getSimpleName().equals("int")){
				toMutateVal = (CtLiteral<Integer>) variables.get(j);
				System.out.println(variables.get(j).getType().getSimpleName());
				i = j;
				break;
			}


		}
		
		if(toMutateVal == null){
			throw new NullPointerException();
		}
		
		/*int newVal = returnRandomValue();
		while(toMutateVal.getValue() == newVal)
			newVal = returnRandomValue();

		switch(r.nextInt(4)){
			case 0:
				toMutateVal.setValue(toMutateVal.getValue() + newVal); 
			case 1:
				toMutateVal.setValue(toMutateVal.getValue() - newVal);
			case 2:
				toMutateVal.setValue(newVal); 

		}*/
		toMutateVal.setValue(toMutateVal.getValue() - 1);
		
	}
	
	public int returnRandomValue(){
	
		return IntValues.values()[r.nextInt(IntValues.values().length)].getValue();
	}
	
	public CtLiteral<?> returnRandomVariable(List<CtLiteral<?>> variables){
		int rd = r.nextInt(variables.size());
		while(!variables.get(rd).getType().getSimpleName().equals("int"))
			rd = r.nextInt(variables.size());
		return variables.get(rd);
	}
	
	public void processingDone(){
//		System.out.println("done");
	}

}
