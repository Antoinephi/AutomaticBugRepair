package processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class TestProcessor extends AbstractProcessor<CtClass<?>> {

	public void init(){
//		System.out.println(this.getEnvironment().toString());
	}
	
	public void process(CtClass<?> c) {
		if(!c.getSimpleName().contains("Test")){
				
		}
	}
	
	public void processingDone(){
		System.out.println("done");
	}

}
