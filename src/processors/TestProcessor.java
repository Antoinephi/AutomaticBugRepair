package processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class TestProcessor extends AbstractProcessor<CtClass<?>> {

	public void init(){
//		System.out.println(this.getEnvironment().toString());
	}

	@Override
	public void process(CtClass<?> arg0) {
//		System.out.println("nok");
	}
	
	public void processingDone(){
		System.out.println("done");
	}

}
