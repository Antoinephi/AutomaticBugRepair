
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import spoon.Launcher;

public class Main {

//	private static final String INPUT_PROCESSOR = "processors.TestProcessor";
//	private static final String INPUT_DATASET = "C:\\Users\\kevin\\Desktop\\wk-spoon\\IntroClassJava-master\\dataset";
//
//	private static void launchSpoon(String projectPath) throws Exception {
//
//		String[] spoonArgs = { "-i", projectPath, "-p", INPUT_PROCESSOR, "-x"
//
//		};
//
//		Launcher.main(spoonArgs);
//
//	}

//	private static List<String> findSourceFolder(String path) {
//		File[] files = new File(path).listFiles();
//		List<String> pathToSources = new ArrayList<String>();
//		for (File f : files) {
//			if (f.isDirectory() && !f.getName().equals("src"))
//				pathToSources.addAll(findSourceFolder(f.getAbsolutePath()));
//			else if (f.isDirectory())
//				pathToSources.add(f.getAbsolutePath());
//		}
//
//		return pathToSources;
//	}

	private static List<String> findTestFolder(String path) {
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		for (File f : files) {
			if (f.isDirectory())
				pathToSources.addAll(findTestFolder(f.getAbsolutePath()));
			else if (f.getName().endsWith(".java"))
				pathToSources.add(f.getAbsolutePath());
		}

		return pathToSources;
	}
	
	private static void convertToClassName(List<String> testFolder,String projetPath) {
		for(int i =0;i<testFolder.size();i++){
			String classe = testFolder.get(i);
			classe  = classe.replace(projetPath, "").replace('\\', '.').replace(".java", "");
			testFolder.remove(i);
			testFolder.add(i,classe);
		}
		
	}

	public static void main(String[] args) throws Exception {
		String projetPath = "C:\\Users\\kevin\\Desktop\\wk-spoon\\prjTest\\src\\test\\java\\";
		List<String> testFolder = findTestFolder(projetPath);
		convertToClassName(testFolder, projetPath);

		JUnitCore junit = new JUnitCore();

		for (String test : testFolder) {

			Class<?> classe = null;
			try {

				classe = Class.forName(test);
				Result results = junit.run(classe);
				System.out.println("Test run: "+test+" nbr fail:"+results.getFailureCount()+"/"+results.getRunCount());
				System.out.println("fail: ");
				for (Failure fail : results.getFailures()) {
					System.out.println();
					System.out.println(fail.getTestHeader());
					System.out.println(fail.getMessage());
					System.out.println();

				}
				System.out.println("Fin Test run: "+test);
				System.out.println();
				
			} catch (Exception e) {
				System.out.println("Impossible d'instancier la classe");
			}

		}

		// String sourceFilePath =
		// "C:\\Users\\kevin\\Desktop\\wk-spoon\\IntroClassJava\\dataset\\checksum\\"
		// +
		// "2c1556672751734adf9a561fbf88767c32224fca14a81e9d9c719f18d0b21765038acc16ecd8377f74d4f43e8c844538161d869605e3516cf797d0a6a59f1f8e"
		// +
		// "\\003\\src\\main\\java\\introclassJava\\checksum_2c155667_003.java";
		// File f = new File(sourceFilePath);
		// System.out.println(sourceFilePath);
		// System.out.println(f.getPath());
		//// System.setProperty("java.home", "C:\\Program
		// Files\\Java\\jdk1.7.0_75"); //to change java home if misconfiguration
		// of eclipse
		// System.out.println(System.getProperty("java.home"));
		// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// int result = compiler.run(System.in, System.out, System.err,
		// f.getPath());
		// File sourceFolder = new
		// File("C:\\Users\\kevin\\Desktop\\wk-spoon\\IntroClassJava\\dataset\\checksum\\"
		// +
		// "2c1556672751734adf9a561fbf88767c32224fca14a81e9d9c719f18d0b21765038acc16ecd8377f74d4f43e8c844538161d869605e3516cf797d0a6a59f1f8e"
		// + "\\003\\src\\main\\java\\introclassJava");
		// URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {
		// new
		// File("C:\\Users\\kevin\\Desktop\\wk-spoon\\IntroClassJava\\dataset\\checksum\\"
		// +
		// "2c1556672751734adf9a561fbf88767c32224fca14a81e9d9c719f18d0b21765038acc16ecd8377f74d4f43e8c844538161d869605e3516cf797d0a6a59f1f8e"
		// +
		// "\\003\\src\\main\\java\\introclassJava\\checksum_2c155667_003.class").toURI().toURL()
		// });

		// List<String> list = findSourceFolder(INPUT_DATASET);
		// for(String s : list){
		// System.out.println("Launching Spoon on " + s.split("\\\\")[6] + " " +
		// s.split("\\\\")[7]+ " " + s.split("\\\\")[8]);
		// launchSpoon(s);
		// }
	}


}