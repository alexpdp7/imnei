package net.pdp7.imnei.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.pdp7.imnei.server.grpc.impl.ChatProtoServer;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		Server server = ServerBuilder.forPort(0).addService(new ChatProtoServer()).build();
		server.start();
		System.out.println(server.getPort());
		server.awaitTermination();
	}
}
