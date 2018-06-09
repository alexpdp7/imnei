package net.pdp7.imnei.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.pdp7.imnei.server.grpc.impl.ImneiProtoImpl;

public class ImneiServerMain {
	Logger logger = LoggerFactory.getLogger(getClass());

	public void start() throws Exception {
		try (GenericContainer<?> kafkaContainer = new GenericContainer<>("spotify/kafka:latest")) {
			kafkaContainer.start();
			Server server = ServerBuilder.forPort(0).addService(new ImneiProtoImpl(new ImneiServer())).build();
			server.start();
			logger.info("running on port {}", server.getPort());
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws Exception {
		new ImneiServerMain().start();
	}
}
