package net.pdp7.imnei.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import com.google.common.collect.ImmutableMap;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.pdp7.imnei.server.grpc.impl.ImneiProtoImpl;

public class ImneiServerMain {
	Logger logger = LoggerFactory.getLogger(getClass());

	public void start() throws Exception {
		try(GenericContainer<?> kafkaContainer = new GenericContainer<>("spotify/kafka:latest")) {
			kafkaContainer.start();
			Map<String, Object> kafkaConfig = ImmutableMap.<String, Object>builder()
					.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
					.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
					.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
					.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
					.put("bootstrap.servers", kafkaContainer.getContainerIpAddress() + ":" + kafkaContainer.getMappedPort(9092))
					.build();
			ImneiMessenger imneiMessenger = new ImneiMessenger(kafkaConfig);
			ImneiServer imneiServer = new ImneiServer(imneiMessenger);
			Server server = ServerBuilder.forPort(0).addService(new ImneiProtoImpl(imneiServer, imneiMessenger)).build();
			server.start();
			logger.info("running on port {}", server.getPort());
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws Exception {
		new ImneiServerMain().start();
	}
}
