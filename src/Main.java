
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
			
	
	private static URLClassLoader classLoader;
	
	private static void launchSpoon(String projectPath) throws Exception{
		
		String[] spoonArgs = { "-i", projectPath,
				"-p", INPUT_PROCESSOR, "-x"
		};
		
		Launcher.main(spoonArgs);
		
	}
	
	private static List<String> findTestFolder(String path, String color) {
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		for (File f : files) {
			if (f.isDirectory())
				pathToSources.addAll(findTestFolder(f.getAbsolutePath(), color));
			else if (f.getName().endsWith(color + ".java"))
				pathToSources.add(f.getAbsolutePath());
		}

		return pathToSources;
	}
	
	private static String convertToClassName(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		//int ind = file.split(File.separator).length;
		return file.split(pattern)[ind-2] + "." + file.split(pattern)[ind -1].replace(".java", "");
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
	
	private static List<String> findJavaFiles(String path){
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		for(File f : files){
			if(f.isDirectory())
				pathToSources.addAll(findJavaFiles(f.getAbsolutePath()));
			else if(f.getName().endsWith(".java"))
				pathToSources.add(f.getAbsolutePath());
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

	
	public static Class<?> compile(String sourcePath) throws MalformedURLException{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		List<String> listSourceFiles = findJavaFiles(sourcePath + "/main/java");
		List<String> listTestFiles = findJavaFiles(sourcePath + "/test/java");

		for(String file : listSourceFiles){
				compiler.run(null, null, null, file);
//				System.out.println(file);
		}
		for(String file : listTestFiles)
			compiler.run(null, null, null, "-cp", sourcePath + "/main/java"+File.pathSeparator+"junit4-4.11.jar", file);
		
//		System.out.println(sourcePath);
		classLoader = URLClassLoader.newInstance(new URL[] {
				new File(sourcePath + "/main/java/").toURI().toURL(), new File(sourcePath +"/test/java/").toURI().toURL()
		});
		
		Class<?> classe = null;
		
	      try {
	  		for(String file : listSourceFiles)
	    		  Class.forName(convertToClassName(file), true, classLoader);
	  		for(String file : listTestFiles)
	  			if(file.contains("Whitebox"))
	  				classe = Class.forName(convertToClassName(file), true, classLoader);

	  		
//	          System.out.println("DOOOOOOOOOONE");
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
		for(Failure failure : results.getFailures()){
			System.out.println(failure);
		}
		return results.getFailures();
	}
	
	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		
		deleteClassFiles(INPUT_DATASET);
		System.out.println("=== Cleaned previous compiled class files === ");
		List<String> sourceFolders = findSourceFolder(INPUT_DATASET);
		Class<?> classe;
		
		for(String folder : sourceFolders){
			classe = compile(folder);
			runTests(classe);
		}

		System.out.println("\n\nTime taken : " + (System.currentTimeMillis() - start) / 1000 + " sec");
				
//		List<String> list = findSourceFolder(INPUT_DATASET);
//		for(String s : list){
//			System.out.println("Launching Spoon on " + s.split("\\\\")[6] + " " + s.split("\\\\")[7]+ " " +  s.split("\\\\")[8]);
//			launchSpoon(s);
//		}
	}
}