package processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parameters.IntValues;
//import parameters.NumericLiteralFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtLiteralImpl;

public class IntMutatorProcessor extends AbstractProcessor<CtClass<?>> {
	private static int intMutationPosition; //position de la variable à muter dnas la classe
	private static int intValuePosition; // position de la valeur dans la liste des mutations possibles 

	public static String currentClass;
	public static Map<Integer, Integer> nbLastingTries;
	public static Map<Integer, Integer> bestIntegerMutation = new HashMap<Integer, Integer>();
	private static Number prevValue;

	/*Flag qui permet de savoir si le lancement des tests apres la mutation a genere de meilleurs resultats*/
	public static boolean better;

	/*Flag qui permet de savoir si toutes les mutations possibles ont ete realisees*/
	public static boolean terminated = false;

	
	private static List<Integer> listOperators = new ArrayList<Integer>();
	
	private List<CtLiteral<Number>> literals = new ArrayList<CtLiteral<Number>>();
	private static List<CtAssignment<?,?>> variables = new ArrayList<CtAssignment<?,?>>();
	
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
				if(better){
					bestIntegerMutation.put(intMutationPosition, intValuePosition);
					better = false;
				}
				
//				System.out.println(">>>>>> new class");
				intMutationPosition = 0;
				intValuePosition = 0;
				prevValue = null;
//				literals = Query.getElements(c, new TypeFilter<CtLiteral<Integer>>(CtLiteral.class));
//				literals = Query.getElements(c, new NumericLiteralFilter());
				variables = Query.getElements(c, new TypeFilter<CtAssignment<?,?>>(CtAssignment.class));
//				System.out.println(literals);
				System.out.println(variables);
				Factory f = getFactory();
				for(CtAssignment<?,?> l : variables){
					if(l.getType().getSimpleName().equals("int")){
//						l.setAssignment(new CtLiteralImpl().setValue(l.getAssignment() + "+" + 1));
//						l.getParent().setFactory(getFactory().Field().createReference((CtField<?>) l.getAssigned()));
//						c.addField((CtField<?>) l.getAssigned());
//						CtCodeSnippetExpression e = new CtCodeSnippetExpressionImpl(); 
//						e.setValue("scanner.nextFloat()");
//						CtBinaryOperator<?> b = new CtBinaryOperatorImpl<>();
//						b.setLeftHandOperand(l.getAssignment());
//						b.setKind(BinaryOperatorKind.PLUS);
//						CtLiteral<Number> cl = new CtLiteralImpl<>();
//						cl.setValue(1);
//						b.setRightHandOperand(cl);
//						l.setAssignment((CtExpression<?>) b);
//						System.out.println(b.getSignature());
//						l.setAssignment((CtExpression) e);
//						System.out.println(l);
//						CtExpression<?> e = l.getAssignment();
						
					}
						
//					System.out.println(l.getAss);
				}
				IntMutatorProcessor.currentClass = this.getClass().getSimpleName();
			}
//			mutate();
			
		}
	}
	
	public void mutate(){
		if(terminated && !bestIntegerMutation.isEmpty()){
			for(int mutationPosition : bestIntegerMutation.keySet()){
				CtLiteral<Number> value = literals.get(mutationPosition);
				value.setValue(value.getValue().intValue() + listOperators.get(bestIntegerMutation.get(mutationPosition)).intValue());
			}
			} else if(terminated){
				return;
			} else {
			
			CtLiteral<Number> value;
			if(intValuePosition == 0 && prevValue != null){
				System.out.println(">>>1");
				value = literals.get(intMutationPosition-1);
				value.setValue(prevValue);
				value = literals.get(intMutationPosition);
			} else if(prevValue != null){
				System.out.println(">>>2");
				value = literals.get(intMutationPosition);
				value.setValue(prevValue);
			} else {
				System.out.println(">>>3");
				value = literals.get(intMutationPosition);
			}
			System.out.print(value.getValue() + " ====> ");
			prevValue = value.getValue();
			
			value.setValue(value.getValue().intValue() + listOperators.get(intValuePosition).intValue());
			System.out.println(value.getValue());
			System.out.println("POSITIONS : \nMutationPosition : " + intMutationPosition + " \nValuePosition : " + intValuePosition);
			if(intValuePosition >= listOperators.size()-1 && intMutationPosition < literals.size()-1){
				intMutationPosition++;
				intValuePosition = 0;
			} else if (intMutationPosition >= literals.size()-1 && intValuePosition >= listOperators.size()-1){
				terminated = true;
			} else {
				intValuePosition++;
			}
		
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
