package mbc.home.com.comp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import mbc.home.com.ICommandLineRunner;
import mbc.home.com.IConsumer;

@SpringBootConfiguration
@Scope("singleton")
//@PropertySource("classpath:KafkaClusterConfig.prop")

public class ConsumerSeek implements IConsumer, ICommandLineRunner {

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
	private KafkaConsumer<String, String> kafkaConsumer;
	private long startingOffset;

	// PostConstruct annotation will automatically be invoked if defined
	@PostConstruct
	@Override
	public int initOperation() {
		// TODO Auto-generated method stub

		System.out.println("In Init Method @PostConstruct ConsumerSeek");

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
		// TODO Auto-generated method stub

		try {
			UUIDK = UUID.randomUUID().toString();

			props.put("enable.auto.commit", "true");
			props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());

			// For all publication retrieval - 'earliest' must be set.
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

			props.put("session.timeout.ms", "30000");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

			List groupIDOption = appArgs.getOptionValues("groupID");
			groupId = (String) groupIDOption.get(0);

			props.put("group.id", groupId);

			List strOffset = appArgs.getOptionValues("startoffset");
			startingOffset = Long.parseLong((String) strOffset.get(0));
		} catch (Exception e) {
			System.out.println("Missing required parm startoffset, aborting.");
			throw e;

		}

		kafkaConsumer = new KafkaConsumer<String, String>(props);
		kafkaConsumer.subscribe(Arrays.asList(topicName), new ConsumerRebalanceListener() {
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				System.out.printf("%s topic-partitions are revoked from this consumer\n",
						Arrays.toString(partitions.toArray()));
			}

			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				System.out.printf("%s topic-partitions are assigned to this consumer\n",
						Arrays.toString(partitions.toArray()));
				Iterator<TopicPartition> topicPartitionIterator = partitions.iterator();
				while (topicPartitionIterator.hasNext()) {
					TopicPartition topicPartition = topicPartitionIterator.next();
					System.out.println("Current offset is " + kafkaConsumer.position(topicPartition)
							+ " committed offset is ->" + kafkaConsumer.committed(topicPartition));
					if (startingOffset == -2) {
						System.out.println("Leaving it alone");
					} else if (startingOffset == 0) {
						System.out.println("Setting offset to begining");

						kafkaConsumer.seekToBeginning(partitions);
					} else if (startingOffset == -1) {
						System.out.println("Setting it to the end ");

						kafkaConsumer.seekToEnd(partitions);
					} else {
						System.out.println("Resetting offset to " + startingOffset);
						kafkaConsumer.seek(topicPartition, startingOffset);
					}
				}
			}
		});
		// Start processing messages
		try {
			while (true) {
				ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.printf("offset = %d, key = %s, partition = %d, value = %s\n", record.offset(),
							record.key(), record.partition(), record.value());
				}
				if (startingOffset == -2)
					kafkaConsumer.commitSync();
			}
		} catch (WakeupException ex) {
			System.out.println("Exception caught " + ex.getMessage());
		} finally {
			kafkaConsumer.close();
			System.out.println("After closing KafkaConsumer");
		}
	}

	public KafkaConsumer<String, String> getKafkaConsumer() {
		return this.kafkaConsumer;
	}

	@PreDestroy
	public void cleanup() {
		System.out.println("De scoping and emptying connection pools");

	}

	@Override
	public void run(String... strings) throws Exception {
		this.starConsumer();

	}

}
