
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import processors.BinaryOperatorProcessor;
import processors.IntMutatorProcessor;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;

public class Main {

	private static final String INPUT_DATASET = ".." + File.separator + "IntroClassJava" + File.separator + "dataset";
	private static final String INPUT_DATASET_CHECKSUM = INPUT_DATASET+File.separator+"checksum";
	private static final String INPUT_DATASET_DIGITS = INPUT_DATASET+File.separator+"digits";
	private static final String INPUT_DATASET_GRADE = INPUT_DATASET+File.separator+"grade";
	private static final String INPUT_DATASET_MEDIAN = INPUT_DATASET+File.separator+"median";
	private static final String INPUT_DATASET_SMALLEST = INPUT_DATASET+File.separator+"smallest";
	private static final String INPUT_DATASET_SYLLABLES= INPUT_DATASET+File.separator+"syllables";


	private static final String WHITEBOX_TEST = "WhiteboxTest";
//	private static final String BLACKBOX_TEST = "BlackboxTest";
	private static final String SPOON_REPERTOIRE= "spooned";
	private static final String SPOON_CLASS_REPERTOIRE="spooned-classes";
	private static final String END_TEST_NAME = "Test.java";
	private static final String PACKAGE ="introclassJava.";
	private static final String FILE_RESULT_NAME ="result.txt";
	private static BinaryOperatorProcessor binaryOperatorProcessor = new BinaryOperatorProcessor();
	
	public static List<String> listSourceFiles;
	public static List<String> listTestFiles;
	
	private static List<AbstractProcessor<?>> listProcessors  = new LinkedList<AbstractProcessor<?>>();
	
	private static void launchSpoon(String projectPath, AbstractProcessor<?> p) throws Exception{
		
		String repertoireCible = projectPath;
		String pattern = Pattern.quote(File.separator);
		if(projectPath.split(pattern).length > 2){
			repertoireCible = SPOON_REPERTOIRE+File.separator+generateRepertoireName(projectPath);
		}
		String[] spoonArgs = { "-i", projectPath, "--compile", "-o", repertoireCible};
		
			Launcher l = new Launcher();
			if(p!=null){
				l.addProcessor(p);
			}
			l.run(spoonArgs);
		
	}
	
	/* transforme le nom d un fichier en nom de classe*/
	private static String convertToClassName(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		return file.split(pattern)[ind-2] + "." + file.split(pattern)[ind -1].replace(".java", "");
	}
	
	private static String generateRepertoireName(String projectPath) {
		String pattern = Pattern.quote(File.separator);
		String[] chaines = projectPath.split(pattern);
		return chaines[chaines.length-4]+"-"+chaines[chaines.length-3]+"-"+chaines[chaines.length-2];
	}
	
	/* permet de recuperer les repertoires de chaque projet*/
	private static List<String> findSourceFolder(String path){
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		for(File f : files){
			if(f.isDirectory() && !f.getName().equals("src"))
				pathToSources.addAll(findSourceFolder(f.getAbsolutePath()));
			else if(f.isDirectory())
				pathToSources.add(f.getAbsolutePath());
		}
		
		return pathToSources;
	}
	
	/*Permet de recuperer tous les fichiers java d un projet ou uniquement le fichier de nom classUnderTest il n'est pas null*/
	private static List<String> findJavaFiles(String path, String classUnderTest){
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		for(File f : files){
			if(f.isDirectory())
				pathToSources.addAll(findJavaFiles(f.getAbsolutePath(),classUnderTest));
			else if((classUnderTest == null && f.getName().endsWith(".java")) ||
					(classUnderTest != null && f.getName().contains(classUnderTest) && f.getName().endsWith(".java"))){
				pathToSources.add(f.getAbsolutePath());
			}
		}
		
		return pathToSources;
	}
	
	private static void deleteClassFiles(String path){
		File[] files = new File(path).listFiles();
		if(files != null){
			for(File f : files){
				if(!f.isDirectory() && f.getName().contains(".class"))
					f.delete();
				else if(f.isDirectory())
					deleteClassFiles(f.getAbsolutePath());
			}
		}
		
	}
	
	public static String getWhiteTestClassNameFromProject(String sourcePath){
		List<String> tests = findJavaFiles(sourcePath + "/test/java",null);
		for(String test : tests){
			if(test.contains(WHITEBOX_TEST)){
				return convertToClassName(test);
			}
		}
		
		return null;
	}
	
	public static String getMainClassNameFromProjectWithoutPackage(String sourcePath){
		String whiteTest = getWhiteTestClassNameFromProject(sourcePath);
		return whiteTest.replace(WHITEBOX_TEST, "").replace(PACKAGE, "");
	}
	
	public static void addResultToFile(String folderName, int nbrFailInitial, int nbrFailFinal){
		String ligne = "projet: "+folderName+" nombre fail initial: "+nbrFailInitial+" nombre de fail final: "+nbrFailFinal+"\n";
		addLigneToResult(ligne);	
	}
	
	public static void addDateToFile(){
		DateFormat fullDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
		addLigneToResult(fullDateFormat.format(new Date())+"\n\n");
	}

	private static void addLigneToResult(String ligne) {
		FileWriter dw = null;
		try {
			dw = new FileWriter(FILE_RESULT_NAME,true);
			dw.write(ligne);
			dw.flush();
			dw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				dw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		final Integer LIMITE_NBR_PROJECT_FOR_DEV = 1800;
		TestLauncher testLauncher = new TestLauncher();
		listProcessors.add(new BinaryOperatorProcessor());
		listProcessors.add(new IntMutatorProcessor());
		addDateToFile();
		
		List<String> sourceFolders = /*findSourceFolder(INPUT_DATASET_DIGITS);*/ new ArrayList<>(); 
		sourceFolders.add("C:\\Users\\kevin\\Desktop\\wk-spoon\\AutomaticBugRepair\\..\\IntroClassJava\\dataset\\digits\\08c7ea4ac39aa6a5ab206393bb4412de9d2c365ecdda9c1b391be963c1811014ed23d2722d7433b8e8a95305eee314d39da4950f31e01f9147f90af91a5c433a\\000\\src");
		int i = 1;

		for(String folder : sourceFolders){
			String repertoireName = SPOON_REPERTOIRE+File.separator+generateRepertoireName(folder);
			String whiteTestCurrent = getWhiteTestClassNameFromProject(folder);
			deleteClassFiles(SPOON_CLASS_REPERTOIRE);
			launchSpoon(folder,null);
			
			int nbrFailInit = testLauncher.runTests(whiteTestCurrent);
			int lowestFail = nbrFailInit;

			//on reinitialise les attributs static du processeur pour eviter d'interferer entre les projets
			BinaryOperatorProcessor.raz((getMainClassNameFromProjectWithoutPackage(folder)));
			if(lowestFail > 0){
				//tant qu il reste des possiblites de mutation on boucle sur les projets generes par spoon
				while(BinaryOperatorProcessor.terminated != true){
					BinaryOperatorProcessor.alreadyMuted = false;
					deleteClassFiles(repertoireName);
					launchSpoon(repertoireName, binaryOperatorProcessor);

					int nbrFailAfterSpoon = testLauncher.runTests(whiteTestCurrent);
					if(nbrFailAfterSpoon < lowestFail){
						System.out.println("correction detectee !" +nbrFailAfterSpoon+ " < "+lowestFail);
						lowestFail = nbrFailAfterSpoon;
						BinaryOperatorProcessor.better = true;
					}
				}
				//on lance spoon une derniere fois pour que les meilleurs mutations trouvees soient restorees
				deleteClassFiles(repertoireName);
				launchSpoon(repertoireName, binaryOperatorProcessor);
			}
			int nbrfailFinal = testLauncher.runTests(whiteTestCurrent);
			addResultToFile(folder,nbrFailInit,nbrfailFinal);
			if(i >= LIMITE_NBR_PROJECT_FOR_DEV)
				break;
			i++;
			System.out.println();
			System.out.println();
		}

		System.out.println("\n\nTime taken : " + (System.currentTimeMillis() - start) / 1000 + " sec");
	}
}