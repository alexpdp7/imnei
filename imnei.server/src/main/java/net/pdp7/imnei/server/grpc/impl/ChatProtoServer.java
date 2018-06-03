package net.pdp7.imnei.server.grpc.impl;

import io.grpc.stub.StreamObserver;
import net.pdp7.imnei.server.grpc.HelloReply;
import net.pdp7.imnei.server.grpc.HelloRequest;
import net.pdp7.imnei.server.grpc.GreeterGrpc.GreeterImplBase;

public class ChatProtoServer extends GreeterImplBase{

	@Override
	public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
		responseObserver.onNext(HelloReply.newBuilder().setMessage("hola " + request.getName()).build());
		responseObserver.onCompleted();
	}
}
