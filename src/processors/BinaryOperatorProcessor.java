package processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtClass;

public class BinaryOperatorProcessor extends AbstractProcessor<CtBinaryOperator<?>> {
	
	/*Permet de ne muter que la classe du projet courant*/
	public static String currentClass;
	public static List<BinaryOperatorKind> binaryOperatorNumber = new ArrayList<>();
	public static List<BinaryOperatorKind> binaryOperatorBoolean = new ArrayList<>();
	public static List<BinaryOperatorKind> binaryOperatorShift = new ArrayList<>();
	public static List<BinaryOperatorKind> binaryOperatorLogic = new ArrayList<>();
	public static boolean alreadyMuted = false;
	/* Map qui permet de retrouver le nombre de tentative restante par CtBinaryOperator*/
	public static Map<Integer, Integer> nbrTentativeRestanteParCtBinaryOperator;
	
	/* Map qui permet de conserver l operateur binaire initial ou celui qui a eu le meilleur score aux tests*/
	public static Map<Integer, BinaryOperatorKind> bestBinaryOperator;
	
	/*Flag qui permet de savoir si le lancement des tests apres la mutation a genere de meilleurs resultats*/
	public static boolean better;
	
	/*Flag qui permet de savoir si toutes les mutations possibles ont ete realisees*/
	public static boolean terminated = false;
	static {
		
		binaryOperatorShift.add(BinaryOperatorKind.USR);
		binaryOperatorShift.add(BinaryOperatorKind.SR);
		binaryOperatorShift.add(BinaryOperatorKind.SL);

		
		binaryOperatorLogic.add(BinaryOperatorKind.AND);
		binaryOperatorLogic.add(BinaryOperatorKind.OR);
		binaryOperatorLogic.add(BinaryOperatorKind.BITAND);
		binaryOperatorLogic.add(BinaryOperatorKind.BITOR);
		binaryOperatorLogic.add(BinaryOperatorKind.BITXOR);
		
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
		String parentSimpleName = binaryOperatorLine.getParent(CtClass.class).getSimpleName();
		if(!(parentSimpleName.contains("Test")) && !(parentSimpleName.contains("Obj")) && parentSimpleName.contains(currentClass)){
			
			/*Si la derniere execution a corrige plus de test on sauvegarde la mutation effectuee*/
			if(better){
				bestBinaryOperator.put(generateIdentifier(binaryOperatorLine), binaryOperatorLine.getKind());
				better = false;
			}
			if(binaryOperatorBoolean.contains(binaryOperatorLine.getKind())){
				nextMutation(binaryOperatorLine, binaryOperatorBoolean);
			}else if(binaryOperatorNumber.contains(binaryOperatorLine.getKind())){
				nextMutation(binaryOperatorLine, binaryOperatorNumber);
			}else if(binaryOperatorLogic.contains(binaryOperatorLine.getKind())){
				nextMutation(binaryOperatorLine, binaryOperatorLogic);
			}else if(binaryOperatorShift.contains(binaryOperatorLine.getKind())){
				nextMutation(binaryOperatorLine, binaryOperatorShift);
			}else{
				return;
			}
			
		}
		
	}



	/* Permet d effectuer la mutation suivante en se basant sur le nombre de tentative restante
	 * 
	 * Si il n y a plus de tentative possible on set l operateur qui a obtenu le meilleur resultat*/
	private void nextMutation(CtBinaryOperator<?> binaryOperatorLine, List<BinaryOperatorKind> binaryOperatorContainer) {
		Integer nbrTentativeRestante = nbrTentativeRestanteParCtBinaryOperator.get(generateIdentifier(binaryOperatorLine));
		if(nbrTentativeRestante == null){
			nbrTentativeRestante = binaryOperatorContainer.size()-1;
			nbrTentativeRestanteParCtBinaryOperator.put(generateIdentifier(binaryOperatorLine), nbrTentativeRestante);
			bestBinaryOperator.put(generateIdentifier(binaryOperatorLine), binaryOperatorLine.getKind());
		}else if(nbrTentativeRestante == -1){
			binaryOperatorLine.setKind(bestBinaryOperator.get(generateIdentifier(binaryOperatorLine)));
			return;
		}
		if(!alreadyMuted){
			binaryOperatorLine.setKind(binaryOperatorContainer.get(nbrTentativeRestante));
			nbrTentativeRestante--;
			nbrTentativeRestanteParCtBinaryOperator.put(generateIdentifier(binaryOperatorLine),nbrTentativeRestante);
			alreadyMuted = true;
		}
	}
	
	/* genere un identifiant unique pour stocker les valeurs dans les maps*/
	private int generateIdentifier(CtBinaryOperator<?> operator){
		int longueurMembreGauche = operator.getLeftHandOperand().toString().length();
		int longueurMembreDroite = operator.getRightHandOperand().toString().length();
		int longueur = operator.getLeftHandOperand().toString().charAt(longueurMembreGauche-1)+operator.getRightHandOperand().toString().charAt(longueurMembreDroite-1);
		//System.out.println(operator + " "+ longueur);
		return operator.getPosition().hashCode()+longueur;
	}
	
	
	public void processingDone(){
		
		for(Integer tentativeRestante : nbrTentativeRestanteParCtBinaryOperator.values()){
			if(tentativeRestante > -1){
				return;
			}
		}
		
		terminated = true;
		
	}



	public static void raz(String classe) {
		BinaryOperatorProcessor.better = false;
		BinaryOperatorProcessor.terminated = false;
		BinaryOperatorProcessor.bestBinaryOperator = new HashMap<>();
		BinaryOperatorProcessor.nbrTentativeRestanteParCtBinaryOperator = new HashMap<>();	
		BinaryOperatorProcessor.currentClass = classe;

	}

}
