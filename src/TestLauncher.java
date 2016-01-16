import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestLauncher {
	
	
	public ClassLoader getClassLoader(String repertoire) throws MalformedURLException {
		final URL[] urls = {
				new File(repertoire).toURI().toURL()};
		return URLClassLoader.newInstance(urls/*, getClass().getClassLoader()*/);
	}
	
	public int runTests(String classeName, String repertoireClasse){
		
		try{
			ClassLoader classLoader = getClassLoader(repertoireClasse);
			JUnitCore junit = new JUnitCore();
			Result results = junit.run(classLoader.loadClass(classeName));

			return results.getFailures() != null ? results.getFailures().size() : 0;
		}catch(Exception e){
			return -1;
		}
		

	}

}
