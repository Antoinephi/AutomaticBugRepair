package processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class TestProcessor extends AbstractProcessor<CtClass<?>> {

	public void init(){
//		System.out.println(this.getEnvironment().toString());
	}
	
	public void process(CtClass<?> c) {
		if(c.getSimpleName().contains("Test")){
			Class<?> classe = null;
			System.out.println(c.getParent().getParent()+"."+c.getParent() + "."+c.getParent() + "."+c.getParent() + "."+c.getParent() + "."+c.getParent() + "." +c.getSimpleName());
//		      try {
//		          classe = Class.forName(c.getParent()+"."+c.getSimpleName());
//		          System.out.println("DOOOOOOOOOONE");
//		       }
//		       catch(Exception e) {
//		          System.out.println("Impossible d'instancier la classe "+c.getParent()+"."+c.getSimpleName());
//		       }
//		    JUnitCore junit = new JUnitCore();
//			Result results = junit.run(c.getActualClass());
//			for(Failure f : results.getFailures()){
//				System.out.println(f.toString());
//			}
		}
//		System.out.println("nok");
	}
	
	public void processingDone(){
		System.out.println("done");
	}

}
