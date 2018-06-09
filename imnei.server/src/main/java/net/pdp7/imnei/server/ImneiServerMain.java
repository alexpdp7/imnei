package net.pdp7.imnei.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.pdp7.imnei.server.grpc.impl.ImneiProtoImpl;

public class ImneiServerMain {
	public static void main(String[] args) throws Exception {
		Server server = ServerBuilder.forPort(0).addService(new ImneiProtoImpl(new ImneiServer())).build();
		server.start();
		System.out.println(server.getPort());
		server.awaitTermination();
	}
}
