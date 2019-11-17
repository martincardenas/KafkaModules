package mbc.home.com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class KafkaModApp implements CommandLineRunner, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	



	private static Logger LOG = LoggerFactory
		      .getLogger(KafkaModApp.class);

	public static void main(String[] args) {
		SpringApplication.run(KafkaModApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("EXECUTING : command line runner");
		  
        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
            
    		
    		if (args.length < 2) {
    			System.out.println("Usage: SimpleConsumer kafkaCluster.properties topicName groupID");
    			return;
    		}

    		if (args.length == 0) {
    			System.out.println("Enter topic name");
    			return;
    		}

    		// Read the configuration class

    		// AnnotationConfigApplicationContext context =
    		// new AnnotationConfigApplicationContext(KafkaPOConfig.class);
    		
    		String beanName = args[0];
            final ICommandLineRunner myCommandLineRunner = applicationContext.getBean(beanName, ICommandLineRunner.class);
            myCommandLineRunner.run(args);

    
    		
    	}
            
            
            
        }
		
	

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
		
		
	}

}
