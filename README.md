# KafkaModules
Kafka Utilies to pub and sub.

SpringBoot application to execute Kafka modules like PUB, and SUB.

To execute: 

java -jar ./target/KafkaModules-0.0.1-SNAPSHOT.jar simpleConsumer --topic=test-topic --groupID=group1

java -cp ./target/KafkaPOCMVN-0.0.1-SNAPSHOT.jar -Dloader.main=mbc.home.com.MyProducerApp org.springframework.boot.loader.PropertiesLauncher --topic=test-topic --groupID=group1



