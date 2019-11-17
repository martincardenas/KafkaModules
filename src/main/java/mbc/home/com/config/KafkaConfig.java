package mbc.home.com.config;

import org.springframework.boot.SpringBootConfiguration;

import org.springframework.context.annotation.Bean;

import mbc.home.com.IConsumer;
import mbc.home.com.IProducerCmd;
import mbc.home.com.comp.SimpleConsumer;
import mbc.home.com.comp.SimpleProducer;


// NOTE: IN SPRING BOOT THESE DEFINITIONS ARE REDUNDUNT AS @Autowired annotation automatically
// declares the Bean based on the scanned classes, these can all be omitted and still work.

// In SB this declaration will throw an exception at runtime not in compile.


@SpringBootConfiguration
public class KafkaConfig {
	/*
	@Bean
	public IConsumer simpleConsumer() {
		

		return new SimpleConsumer();

	}


	@Bean
	public IProducerCmd simpleProducer() {
		

		return new SimpleProducer();

	}
	*/
	

}
