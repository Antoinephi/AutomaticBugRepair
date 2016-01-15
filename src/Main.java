
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import processors.BinaryOperatorProcessor;
import processors.IntMutatorProcessor;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;

public class Main {

	private static final String INPUT_DATASET = ".." + File.separator + "IntroClassJava" + File.separator + "dataset";
	private static final String WHITEBOX_TEST = "WhiteboxTest";
//	private static final String BLACKBOX_TEST = "BlackboxTest";
	private static final String SPOON_REPERTOIRE= "spooned";
	private static final String SPOON_CLASS_REPERTOIRE="spooned-classes";
	private static final String END_TEST_NAME = "Test.java";
	private static final String PACKAGE ="introclassJava.";
	private static BinaryOperatorProcessor binaryOperatorProcessor = new BinaryOperatorProcessor();
	
	public static List<String> listSourceFiles;
	public static List<String> listTestFiles;
	
	private static List<AbstractProcessor<?>> listProcessors  = new LinkedList<AbstractProcessor<?>>();
	
	private static void launchSpoon(String projectPath, AbstractProcessor<?> p) throws Exception{
		
		String[] spoonArgs = { "-i", projectPath, "--compile"};
		
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
		for(File f : files){
			if(!f.isDirectory() && f.getName().contains(".class"))
				f.delete();
			else if(f.isDirectory())
				deleteClassFiles(f.getAbsolutePath());
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
	
	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		final Integer LIMITE_NBR_PROJECT_FOR_DEV = 3;
		TestLauncher testLauncher = new TestLauncher();
		listProcessors.add(new BinaryOperatorProcessor());
		listProcessors.add(new IntMutatorProcessor());
		
		
		List<String> sourceFolders = findSourceFolder(INPUT_DATASET);
		int i = 1;

		for(String folder : sourceFolders){
			System.out.println("projet sous analyse: "+folder);
			System.out.println("Création du projet dans spooned");
			String whiteTestCurrent = getWhiteTestClassNameFromProject(folder);
			deleteClassFiles(SPOON_CLASS_REPERTOIRE);
			launchSpoon(folder,null);
			
			int nbrFailInit = testLauncher.runTests(whiteTestCurrent);
			System.out.println("Nombre de fail initial:"+nbrFailInit);

			//on reinitialise les attributs static du processeur pour eviter d'interferer entre les projets
			BinaryOperatorProcessor.raz((getMainClassNameFromProjectWithoutPackage(folder)));
			if(nbrFailInit > 0){
				//tant qu il reste des possiblites de mutation on boucle sur les projets generes par spoon
				while(BinaryOperatorProcessor.terminated != true){
					deleteClassFiles(SPOON_CLASS_REPERTOIRE);
					launchSpoon(SPOON_REPERTOIRE, binaryOperatorProcessor);

					int nbrFailAfterSpoon = testLauncher.runTests(whiteTestCurrent);
					System.out.println("nbr fail apres mutation "+nbrFailAfterSpoon);
					if(nbrFailAfterSpoon < nbrFailInit){
						nbrFailInit = nbrFailAfterSpoon;
						BinaryOperatorProcessor.better = true;
					}
				}
				//on lance spoon une derniere fois pour que les meilleurs mutations trouvees soient restorees
				deleteClassFiles(SPOON_CLASS_REPERTOIRE);
				launchSpoon(SPOON_REPERTOIRE, binaryOperatorProcessor);
			}
			System.out.println("nbr fail final: "+testLauncher.runTests(whiteTestCurrent));

			if(i >= LIMITE_NBR_PROJECT_FOR_DEV)
				break;
			i++;
			System.out.println();
			System.out.println();
		}

		System.out.println("\n\nTime taken : " + (System.currentTimeMillis() - start) / 1000 + " sec");
	}
}