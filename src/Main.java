
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
	private static final String BLACKBOX_TEST = "BlackboxTest";
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
		String repertoireDestinationClasse = "";
		String pattern = Pattern.quote(File.separator);
		String [] repertoireSplit = projectPath.split(pattern);
		if(repertoireSplit.length > 2){
			repertoireCible = SPOON_REPERTOIRE+File.separator+generateRepertoireName(projectPath);
			repertoireDestinationClasse = SPOON_CLASS_REPERTOIRE+File.separator+generateRepertoireName(projectPath);
		}else{
			repertoireDestinationClasse = SPOON_CLASS_REPERTOIRE+File.separator+repertoireSplit[repertoireSplit.length-1];
			
		}
		String[] spoonArgs = { "-i", projectPath, "--compile", "-o", repertoireCible, "-d" ,repertoireDestinationClasse };
		
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
	
	public static String getBlackTestClassNameFromProject(String sourcePath){
		List<String> tests = findJavaFiles(sourcePath + "/test/java",null);
		for(String test : tests){
			if(test.contains(BLACKBOX_TEST)){
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
		
		List<String> sourceFolders =  findSourceFolder(INPUT_DATASET_DIGITS);//
		
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/digits/d5059e2b1493f91b32bb0c2c846d8461c50356f709a91792b6b625e112675de4edac2a09fa627d58c4651c662bbcf2c477660469b9327ed9427b43c25e4e070c/000/src");
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/digits/c5d8f924b86adfeafa7f520559aeb8bd0c3c178efe2500c4054c5ce51bcdbfc2da2e3d9fd5c73f559a7cb6c3b3555b04646111404744496cbcf31caa90e5beb4/003/src");
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/median/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/003/src");
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/grade/bfad6d21d636def8e9e72910c3eb0815f5747669e3a60fb10c6f7f421082d18e548dcfc5d4717bb6da075c36f067b37858d11528ce796b3226ae33719c5007ce/001/src");
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/grade/bfad6d21d636def8e9e72910c3eb0815f5747669e3a60fb10c6f7f421082d18e548dcfc5d4717bb6da075c36f067b37858d11528ce796b3226ae33719c5007ce/000/src");
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/003/src");
//		sourceFolders.add("/home/m2iagl/once/Documents/wk-spoon/IntroClassJava/dataset/grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/001/src");
				//findSourceFolder(INPUT_DATASET_SYLLABLES);
		int i = 1;

		for(String folder : sourceFolders){
			//on reinitialise les attributs static du processeur pour eviter d'interferer entre les projets
			BinaryOperatorProcessor.raz();
			
			String repertoireClasseName = SPOON_CLASS_REPERTOIRE+File.separator+generateRepertoireName(folder);
			String whiteTestCurrent = getWhiteTestClassNameFromProject(folder);
			String blackTestCurrent = getBlackTestClassNameFromProject(folder);
			deleteClassFiles(repertoireClasseName);
			launchSpoon(folder,null);
			int nbrFailInit = testLauncher.runTests(whiteTestCurrent,repertoireClasseName);
			System.out.println("Projet sous test "+folder+" nbr fail init "+nbrFailInit);

			int lowestFail = nbrFailInit;

			if(lowestFail > 0){
				//tant qu il reste des possiblites de mutation on boucle sur les projets generes par spoon
				while(BinaryOperatorProcessor.terminated != true){
					BinaryOperatorProcessor.alreadyMuted = false;
					deleteClassFiles(repertoireClasseName);
					//launchSpoon(repertoireName, binaryOperatorProcessor);
					launchSpoon(folder, binaryOperatorProcessor);
					int nbrFailAfterSpoon = testLauncher.runTests(whiteTestCurrent,repertoireClasseName);
					if(nbrFailAfterSpoon < lowestFail){
						System.out.println("correction detectee !" +nbrFailAfterSpoon+ " < "+lowestFail);
						lowestFail = nbrFailAfterSpoon;
						BinaryOperatorProcessor.better = true;
					}
				}

			}
			int nbrfailFinal = testLauncher.runTests(whiteTestCurrent,repertoireClasseName);
			if(nbrFailInit != 0 && nbrfailFinal == 0){
				int nbrBlackFail = testLauncher.runTests(blackTestCurrent, repertoireClasseName);
				if(nbrBlackFail == 0){
					addResultToFile(folder+" black and white done ",nbrFailInit,nbrfailFinal);
				}else{
					addResultToFile(folder+" white done ",nbrFailInit,nbrfailFinal);
				}
			}
			if(i >= LIMITE_NBR_PROJECT_FOR_DEV)
				break;
			i++;
			System.out.println();
			System.out.println();
		}

		System.out.println("\n\nTime taken : " + (System.currentTimeMillis() - start) / 1000 + " sec");
	}
}