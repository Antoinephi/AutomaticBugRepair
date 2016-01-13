
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import processors.BinaryOperatorProcessor;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;

public class Main {

	private static final String INPUT_PROCESSOR = "processors.BinaryOperatorProcessor";
	private static final String INPUT_DATASET = ".." + File.separator + "IntroClassJava" + File.separator + "dataset";
	private static final String WHITEBOX_TEST = "WhiteboxTest";
	private static final String BLACKBOX_TEST = "BlackboxTest";
	private static final String SPOON_REPERTOIRE= "./spooned";
	private static final String END_TEST_NAME = "Test.java";
	private static final String PACKAGE ="introclassJava.";
	private static BinaryOperatorProcessor binaryOperatorProcessor = new BinaryOperatorProcessor();
	
	private static URLClassLoader classLoader;
	public static List<String> listSourceFiles;
	public static List<String> listTestFiles;
	
	private static void launchSpoon(String projectPath, AbstractProcessor<?> p) throws Exception{
		
//		String[] spoonArgs = { "-i", projectPath,
//				"-p", INPUT_PROCESSOR, "-x"
//		};
//		
//		Launcher.main(spoonArgs);
		
		String[] spoonArgs = { "-i", projectPath, "-x"
		};
		
			Launcher l = new Launcher();
			l.addProcessor(p);
			l.run(spoonArgs);
		
	}
	
	/* transforme le nom d un fichier en nom de classe*/
	private static String convertToClassName(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		return file.split(pattern)[ind-2] + "." + file.split(pattern)[ind -1].replace(".java", "");
	}
	
	/* transforme le nom d un fichier en nom de classe sans son package*/
	private static String convertToClassNameWithoutPackage(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		return file.split(pattern)[ind -1].replace(".java", "");
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

	public static Class<?> compile(String sourcePath, String classUnderTest) throws MalformedURLException{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String repertoireMain = "";
		String repertoireTest="";
		
		if(classUnderTest == null){
			repertoireMain = "/main/java";
			repertoireTest = "/test/java";

		}
		
		listSourceFiles = findJavaFiles(sourcePath + repertoireMain,classUnderTest);
		listTestFiles = findJavaFiles(sourcePath + repertoireTest,classUnderTest);
		


		for(String file : listSourceFiles){
			if(!file.endsWith(END_TEST_NAME))
				compiler.run(null, null, null, file);
		}
		for(String file : listTestFiles){
			if(file.endsWith(END_TEST_NAME))
				compiler.run(null, null, null, "-cp", sourcePath + repertoireMain+File.pathSeparator+"junit4-4.11.jar", file);
		}
		classLoader = URLClassLoader.newInstance(new URL[] {
				new File(sourcePath + repertoireMain).toURI().toURL(), new File(sourcePath +repertoireTest).toURI().toURL()
		});
		
		Class<?> classe = null;
		
	      try {
	    	  
	    	if(classUnderTest != null){
	    		 classe = classLoader.loadClass(PACKAGE+classUnderTest+WHITEBOX_TEST);
	    		 return classe;
	    	}
	  		for(String file : listSourceFiles){
				if(!file.endsWith(END_TEST_NAME))
					Class.forName(convertToClassName(file), true, classLoader);
	  		}
	  		for(String file : listTestFiles){
	  			if(file.contains(WHITEBOX_TEST))
	  				classe = Class.forName(convertToClassName(file), true, classLoader);
	  		}

	  	   }
	       catch(Exception e) {
	    	   e.printStackTrace();
	          System.out.println("Impossible d'instancier la classe");
	       }
	      
	      return classe;
		
	}
	
	public static int runTests(Class<?> testClass){
		JUnitCore junit = new JUnitCore();
		Result results = junit.run(testClass);

		return results.getFailures() != null ? results.getFailures().size() : 0;
	}
	
	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		final Integer LIMITE_NBR_PROJECT_FOR_DEV = 3;

		System.out.println("=== Cleaned previous compiled class files === ");
		//on recupere les projets
		List<String> sourceFolders = findSourceFolder(INPUT_DATASET);
		Class<?> classe;
		int i = 1;

		for(String folder : sourceFolders){
			deleteClassFiles(INPUT_DATASET);
			System.out.println("projet sous analyse: "+folder);
			//compilation des classes du projet courant
			classe = compile(folder, null);
			//lancement des tests et sauvegarde du nombre de fail initial
			int nbrFail = runTests(classe);
			System.out.println("Nbr fail initial: "+nbrFail);
			//on reinitialise les attributs static du processeur pour eviter d'interferer entre les projets
			BinaryOperatorProcessor.raz(convertToClassNameWithoutPackage(listSourceFiles.get(0)));
			if(nbrFail > 0){
				//tant qu il reste des possiblites de mutation on boucle sur les projets generes par spoon
				while(BinaryOperatorProcessor.terminated != true){
					deleteClassFiles(SPOON_REPERTOIRE);
					launchSpoon(SPOON_REPERTOIRE, binaryOperatorProcessor);
					classe = compile(SPOON_REPERTOIRE, convertToClassNameWithoutPackage(listSourceFiles.get(0)));
					int nbrFailAfterSpoon = runTests(classe);
					System.out.println("nbr fail apres mutation "+nbrFailAfterSpoon);
					if(nbrFailAfterSpoon < nbrFail){
						nbrFail = nbrFailAfterSpoon;
						BinaryOperatorProcessor.better = true;
					}
				}
				
				//on lance spoon une derniere fois pour que les meilleurs mutations trouvees soient restorees
				deleteClassFiles(SPOON_REPERTOIRE);
				launchSpoon(SPOON_REPERTOIRE, binaryOperatorProcessor);
			}
			classe = compile(SPOON_REPERTOIRE, convertToClassNameWithoutPackage(listSourceFiles.get(0)));
			System.out.println("nbr fail final: "+runTests(classe));

			if(i >= LIMITE_NBR_PROJECT_FOR_DEV)
				break;
			i++;
			System.out.println();
			System.out.println();
		}

		System.out.println("\n\nTime taken : " + (System.currentTimeMillis() - start) / 1000 + " sec");
	}
}