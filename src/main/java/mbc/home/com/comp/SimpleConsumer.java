package mbc.home.com.comp;

import java.util.Properties;
import java.util.UUID;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringBootConfiguration;

import mbc.home.com.ICommandLineRunner;
import mbc.home.com.IConsumer;

// NOTE: Explicit bean ID being defined for the search
// @Component("tennisCoach")

// I can do the same by removing the name and the default beanID will be used
// default name is class name in LOWER CASE I.E. tennisCoach

// @Component("tennisCoach") = @Component

@SpringBootConfiguration
@Scope("singleton")
//@PropertySource("classpath:KafkaClusterConfig.properties")
public class SimpleConsumer implements IConsumer, ICommandLineRunner {

	@Value("${cls.kafkaClusterBroker}")
	String clsBroker;
	@Value("${cls.kafkaClusterZookeeper}")
	String zooKeeper;
	@Value("${cls.kafkaClsHost}")
	String clusterHost;
	@Value("${cls.kafkanetClssubnet}")
	String kafkaSubNet;

	@Autowired
	ApplicationArguments appArgs;

	String topicName;
	Properties props;
	String groupId;
	String UUIDK;

	@PostConstruct
	@Override
	public int initOperation() {
		System.out.println("In Init Method @PostConstruct SimpleConsumer");

		String[] kafkaBroker = clsBroker.split(",");

		// Kafka consumer configuration settings

		List topicOption = appArgs.getOptionValues("topic");
		topicName = (String) topicOption.get(0);

		props = new Properties();

		props.put("bootstrap.servers", clusterHost + ":" + kafkaBroker[0]);

		return 0;
	}

	@Override
	public void starConsumer() {
		try {

			List groupIDOption = appArgs.getOptionValues("groupID");
			groupId = (String) (String) groupIDOption.get(0);

			UUIDK = UUID.randomUUID().toString();

			props.put("group.id", groupId);
			props.put("enable.auto.commit", "true");
			props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());

			// For all publication retrieval - 'earliest' must be set.
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

			props.put("session.timeout.ms", "30000");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		} catch (Exception e) {
			System.out.println("Missing required parm startoffset, aborting.");
			throw e;

		}

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

		// Kafka Consumer subscribes list of topics here.
		consumer.subscribe(Arrays.asList(topicName));

		System.out.println("ConsumerID: " + UUIDK + " GroupID: " + groupId);
		System.out.println(" Subscribed to topic " + topicName);

		// print the topic name
		int i = 0;

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records)

				// print the offset,key and value for the consumer records.
				System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
		}


	}

	@Override
	public void run(String... strings) throws Exception {
		// TODO Auto-generated method stub
		
		this.starConsumer();


	}

}
