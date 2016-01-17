package processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parameters.IntValues;
import parameters.NumericLiteralFilter;
//import parameters.NumericLiteralFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

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
	private static List<CtFieldRead<?>> variables = new ArrayList<CtFieldRead<?>>();
	
	static {
		listOperators.add(IntValues.NEGATIVE.getValue());
		listOperators.add(IntValues.POSITIVE.getValue());
//		listOperators.add(+10);
//		listOperators.add(-10);
//		listOperators.add(+100);
//		listOperators.add(-100);
//		listOperators.add(IntValues.MAX_INT.getValue());
//		listOperators.add(IntValues.MIN_INT.getValue());
//		listOperators.add(IntValues.ZERO.getValue());
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
				literals = Query.getElements(c, new NumericLiteralFilter());
				variables = Query.getElements(c, new TypeFilter<CtFieldRead<?>>(CtFieldRead.class));
//				System.out.println(literals);
//				System.out.println(variables);
//				Factory f = getFactory();
//				for(CtFieldRead<?> l : variables){
//					if(l.getType().getSimpleName().equals("int") && !l.toString().contains("args")){
//						l.setAssignment(new CtLiteralImpl().setValue(l.getAssignment() + "+" + 1));
//						l.getParent().setFactory(getFactory().Field().createReference((CtField<?>) l.getAssigned()));
//						c.addField((CtField<?>) l.getAssigned());
//						CtCodeSnippetExpression<Number> e = new CtCodeSnippetExpressionImpl<Number>(); 
//						e.setValue("2");
//						CtBinaryOperator<?> b = new CtBinaryOperatorImpl<>();
//						b.setLeftHandOperand(l);
//						b.setKind(BinaryOperatorKind.PLUS);
//						CtLiteral<Number> cl = new CtLiteralImpl<Number>();
//						cl.setValue(1);
//						b.setRightHandOperand(cl);
//						l.setAssignment((CtExpression<?>) b);
//						System.out.println(b.getSignature());s
//						l.setAssignment((CtExpression) e);
//						l.setAssignment((CtExpression<?>) cl);
//						CtFieldRead<?> field = new CtFieldReadImpl<Number>();
//						field.set
//						CtMethod m = l.getParent(CtMethod.class);
//						e = b.getParent();
//						CtCodeSnippetStatement f = getFactory().Core().createCodeSnippetStatement();
//						f.setValue(l.toString() + " + 1");
//						CtBlock block = getFactory().Code().createCtBlock(f);
//						m.;
//						System.out.println(f);
//						CtExpression<?> e = l.getAssignment();
						
//					}
						
//					System.out.println(l.getAss);
//				}
//				System.exit(0);
				IntMutatorProcessor.currentClass = this.getClass().getSimpleName();
			}
			mutate();
			
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
			if(!value.getParent().toString().contains("args") && value.getValue().intValue() != (value.getValue().intValue() + listOperators.get(intValuePosition).intValue())){
				System.out.print(value.getValue() + " ====> ");
				prevValue = value.getValue();
				if(listOperators.get(intValuePosition).intValue() == 0 || listOperators.get(intValuePosition).intValue() == Integer.MAX_VALUE || listOperators.get(intValuePosition).intValue() == Integer.MIN_VALUE){
					value.setValue(listOperators.get(intValuePosition).intValue());
				} else {
					value.setValue(value.getValue().intValue() + listOperators.get(intValuePosition).intValue());
				}
				System.out.println(value.getValue());
			}
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
