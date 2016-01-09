
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import spoon.Launcher;

public class Main {

	private static final String INPUT_PROCESSOR = "processors.TestProcessor";
	private static final String INPUT_DATASET = ".." + File.separator + "IntroClassJava" + File.separator + "dataset";
	private static final String WHITEBOX_TEST = "WhiteboxTest";
	private static final String BLACKBOX_TEST = "BlackboxTest";
	private static final String SPOON_REPERTOIRE= "./spooned";
	private static final String END_TEST_NAME = "Test.java";
			
	
	private static URLClassLoader classLoader;
	public static List<String> listSourceFiles;
	public static List<String> listTestFiles;
	
	private static void launchSpoon(String projectPath) throws Exception{
		
		String[] spoonArgs = { "-i", projectPath,
				"-p", INPUT_PROCESSOR, "-x"
		};
		
		Launcher.main(spoonArgs);
		
	}
	
	private static String convertToClassName(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		return file.split(pattern)[ind-2] + "." + file.split(pattern)[ind -1].replace(".java", "");
	}
	
	private static String convertToClassNameWithoutPackage(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		return file.split(pattern)[ind -1].replace(".java", "");
	}
	
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
		for(String file : listTestFiles)
			if(file.endsWith(END_TEST_NAME))
				compiler.run(null, null, null, "-cp", sourcePath + repertoireMain+File.pathSeparator+"junit4-4.11.jar", file);
		
		classLoader = URLClassLoader.newInstance(new URL[] {
				new File(sourcePath + repertoireMain).toURI().toURL(), new File(sourcePath +repertoireTest).toURI().toURL()
		});
		
		Class<?> classe = null;
		
	      try {
	  		for(String file : listSourceFiles)
				if(!file.endsWith(END_TEST_NAME))
					Class.forName(convertToClassName(file), true, classLoader);
	  		for(String file : listTestFiles)
	  			if(file.contains(WHITEBOX_TEST))
	  				classe = Class.forName(convertToClassName(file), true, classLoader);

	  	       }
	       catch(Exception e) {
	    	   e.printStackTrace();
	          System.out.println("Impossible d'instancier la classe");
	       }
	      
	      return classe;
		
	}
	
	public static List<Failure> runTests(Class<?> testClass){
		JUnitCore junit = new JUnitCore();
		Result results = junit.run(testClass);
		System.out.println("Classe sous test: " +testClass.getName());
		System.out.println("Nombre de fail: "+results.getFailureCount()+"/"+results.getRunCount());
//		for(Failure failure : results.getFailures()){
//			System.out.println(failure);
//		}
		return results.getFailures();
	}
	
	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		final Integer LIMITE_NBR_PROJECT_FOR_DEV = 5;
		deleteClassFiles(INPUT_DATASET);
		System.out.println("=== Cleaned previous compiled class files === ");
		List<String> sourceFolders = findSourceFolder(INPUT_DATASET);
		Class<?> classe;
		int i = 0;
		for(String folder : sourceFolders){
			System.out.println("projet sous analyse: "+folder);
			classe = compile(folder, null);
			List<Failure> fails = runTests(classe);
			if(!fails.isEmpty()){
				launchSpoon(folder);
			}
			System.out.println();
			System.out.println("résultat après spoon:");
			classe = compile(SPOON_REPERTOIRE, convertToClassNameWithoutPackage(listSourceFiles.get(0)));
			runTests(classe);
			
			if(i == LIMITE_NBR_PROJECT_FOR_DEV)
				break;
			i++;
			System.out.println();
			System.out.println();
		}

		System.out.println("\n\nTime taken : " + (System.currentTimeMillis() - start) / 1000 + " sec");
	}
}