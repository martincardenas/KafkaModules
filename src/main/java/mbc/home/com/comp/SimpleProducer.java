package mbc.home.com.comp;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import mbc.home.com.ICommandLineRunner;
import mbc.home.com.IProducerCmd;

@SpringBootConfiguration
@Scope("singleton")
//@PropertySource("classpath:KafkaClusterConfig.prop")
public class SimpleProducer implements IProducerCmd, ICommandLineRunner {

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
	String keyId;

	String UUIDK;
	String[] kafkaBroker;

	@PostConstruct
	@Override
	public int initOperation() {
		System.out.println("In Init Method @PostConstruct SimpleProduer");

		kafkaBroker = clsBroker.split(",");

		// Kafka consumer configuration settings

		List topicOption = appArgs.getOptionValues("topic");
		topicName = (String) topicOption.get(0);

		props = new Properties();

		// Assign localhost id
		// props.put("bootstrap.servers", "localhost:9092");
		props.put("bootstrap.servers", clusterHost + ":" + kafkaBroker[0]);

		return 0;
	}

	@Override
	public void startProducer() {
		try {

			List KeyIdOption = appArgs.getOptionValues("keyId");

			if (KeyIdOption != null) {
				keyId = (String) KeyIdOption.get(0);
			}

			props.put("bootstrap.servers", clusterHost + ":" + kafkaBroker[0]);

			// Set acknowledgements for producer requests. 1=leader response,0=no
			// confirmation, all=all replicas confirm
			props.put("acks", "all");

			// If the request fails, the producer can automatically retry,
			props.put("retries", 0);

			// Specify buffer size in config
			props.put("batch.size", 16384);

			// Reduce the no of requests less than 0
			props.put("linger.ms", 1);

			// The buffer.memory controls the total amount of memory available to the
			// producer for buffering.
			props.put("buffer.memory", 33554432);

			// Schema.Parser parser = new Schema.Parser();
			// Schema schema = parser.parse(USER_SCHEMA);

			// Injection<GenericRecord, byte[]> recordInjection =
			// GenericAvroCodecs.toBinary(schema);

			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Missing required parm startoffset, aborting.");
			throw e;
		}

		// create instance for properties to access producer configs
		// Properties props = new Properties();

		// Assign localhost id
		// props.put("bootstrap.servers", "localhost:9092");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		System.out.println("Enter PUB");

		while (true) {

			Scanner scanner = new Scanner(System.in);

			// System.out.print("Enter a sentence:\t");
			String sentence = scanner.nextLine();

			if (sentence.toString().equals("")) {
				break;
			}

			String valuex = sentence.toString();

			// CREATE THE PRODUCER RECORD
			ProducerRecord pr = null;

			if (keyId != null)
				pr = new ProducerRecord<>(topicName, keyId, valuex);
			else

				pr = new ProducerRecord<>(topicName, valuex);
			// FIRE FORGET
			producer.send(pr);

		}
		producer.close();
		System.out.println("Done!");

	}

	@Override
	public void run(String... strings) throws Exception {

		this.startProducer();

	}

}
