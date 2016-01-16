package processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import parameters.IntValues;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class IntMutatorProcessor extends AbstractProcessor<CtClass<?>> {
	private static int intMutationPosition; //position de la variable à muter dnas la classe
	private static int intValuePosition; // position de la valeur dans la liste des mutations possibles 

	public static String currentClass;
	public static Map<Integer, Integer> nbLastingTries;
	public static Map<Integer, Integer> bestIntegerMutation;

	/*Flag qui permet de savoir si le lancement des tests apres la mutation a genere de meilleurs resultats*/
	public static boolean better;

	/*Flag qui permet de savoir si toutes les mutations possibles ont ete realisees*/
	public static boolean terminated = false;

	
	private static List<Integer> listOperators = new ArrayList<Integer>();
	
	private static List<CtLiteral<Integer>> literals = new ArrayList<CtLiteral<Integer>>();
	
	static {
		listOperators.add(-1);
		listOperators.add(1);
	}

	public void init(){
//		System.out.println(this.getEnvironment().toString());
	}
	
	public void process(CtClass<?> c) {
		if(!c.getSimpleName().contains("Test") && !c.getSimpleName().contains("Obj")){
			if(!IntMutatorProcessor.currentClass.equals(this.getClass().getSimpleName())){
				literals = Query.getElements(c, new TypeFilter<CtLiteral<Integer>>(CtLiteral.class));
				IntMutatorProcessor.currentClass = this.getClass().getSimpleName();
			}
			
		}
	}
	
	public void mutate(){
		CtLiteral<Integer> value = literals.get(intMutationPosition);
		value.setValue(value.getValue() + listOperators.get(intValuePosition));
		if(intValuePosition >= listOperators.size()-1){
			intMutationPosition++;
			intValuePosition = 0;
		} else {
			intValuePosition++;
		}
		
	}
	
/*	public void returnMutation(){
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

		}
		toMutateVal.setValue(toMutateVal.getValue() - 1);
		
	}*/
	
	public int returnValue(int index){
		return IntValues.values()[index].getValue();
	}
	
	/*public int returnRandomValue(){
	
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
	}*/
	
	public static void raz(String classe) {
		IntMutatorProcessor.better = false;
		IntMutatorProcessor.terminated = false;
		IntMutatorProcessor.intMutationPosition = 0;
		IntMutatorProcessor.intValuePosition = 0;
//		BinaryOperatorProcessor.bestBinaryOperator = new HashMap<>();
//		BinaryOperatorProcessor.nbrTentativeRestanteParCtBinaryOperator = new HashMap<>();	
		IntMutatorProcessor.currentClass = classe;

	}

}
