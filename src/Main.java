

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;

public class Main {

	private static final String INPUT_PROCESSOR = "processors.TestProcessor";
	private static final String INPUT_DATASET = "C:\\Users\\Antoine\\workspace\\IntroClassJava\\dataset";

	private static void launchSpoon(String projectPath) throws Exception{
		
		String[] spoonArgs = { "-i", projectPath,
				"-p", INPUT_PROCESSOR, "-x"
				
		};
		
		Launcher.main(spoonArgs);

		
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

	public static void main(String[] args) throws Exception {
		List<String> list = findSourceFolder(INPUT_DATASET);
		for(String s : list){
			System.out.println("Launching Spoon on " + s.split("\\\\")[6] + " " + s.split("\\\\")[7]+ " " +  s.split("\\\\")[8]);
			launchSpoon(s);
		}
	}
}