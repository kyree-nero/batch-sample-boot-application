package sample;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "master"})
public abstract class AbstractBatchIT  extends AbstractBaseIT{
	@Autowired JobLauncher jobLauncher;
	@Autowired ConfigurableEnvironment env;
	
	
//	protected AbstractApplicationContext startNewSlave(ExecutorService executorService) {
//		AnnotationConfigApplicationContext slaveContext = new AnnotationConfigApplicationContext();
//		Callable<Integer> slave = createSlave(slaveContext);
//		executorService.submit(slave);
//		return slaveContext;
//	}
	
//	private Callable<Integer> createSlave(AnnotationConfigApplicationContext context){
//		return new Callable<Integer>() {
//
//			@Override
//			public Integer call() throws Exception {
//				context.register(
//						PersistenceConfiguration.class, 
//						JmsConfiguration.class,
//						BatchConfiguration.class
//				);
//				ConfigurableEnvironment convenv = (ConfigurableEnvironment)context.getEnvironment();
//				convenv.merge(env);
//				context.getEnvironment().setActiveProfiles("test", "slave");
//				context.refresh();
//				return 0;
//			}
//			
//		};
//	}
}
