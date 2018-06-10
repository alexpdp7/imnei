package net.pdp7.imnei.server;

import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ImneiMessenger {

	protected final KafkaProducer<String, String> producer;
	protected final Map<String, Object> kafkaConfig;

	public ImneiMessenger(Map<String, Object> kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
		this.producer = new KafkaProducer<>(kafkaConfig);
	}
	
	public void sendMessage(String chatId, String message) {
		producer.send(new ProducerRecord<String, String>(chatId, message));
	}

	public ImneiMessengerReceiver createReceiver() {
		return new ImneiMessengerReceiver();
	}
	
	public class ImneiMessengerReceiver {
		protected final KafkaConsumer<String, String> kafkaConsumer;
		protected final Queue<String> pendingSubscriptions = new ConcurrentLinkedQueue<>();

		public ImneiMessengerReceiver() {
			kafkaConsumer = new KafkaConsumer<>(kafkaConfig);
			String myTopic = UUID.randomUUID().toString();
			producer.send(new ProducerRecord<String, String>(myTopic, "foo"));
			kafkaConsumer.subscribe(Arrays.asList(myTopic));
		}

		public void subscribe(String chatId) {
			pendingSubscriptions.add(chatId);
		}
		
		public void start() {
			while(true) {
				System.out.println("polling");
				for(ConsumerRecord<String, String> record : kafkaConsumer.poll(1000)) {
					System.out.println(record);
				}
				System.out.println("polled");
				System.out.println(pendingSubscriptions);
				kafkaConsumer.subscribe(pendingSubscriptions);
			}
		}
	}
}
