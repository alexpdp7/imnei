package net.pdp7.imnei.server.grpc.impl;

import io.grpc.stub.StreamObserver;
import net.pdp7.imnei.server.grpc.ImneiGrpc.ImneiImplBase;
import net.pdp7.imnei.server.grpc.ImneiProto.ConnectNewMessage;
import net.pdp7.imnei.server.grpc.ImneiProto.Connection;

public class ImneiProtoImpl extends ImneiImplBase {

	@Override
	public void connectNew(ConnectNewMessage request, StreamObserver<Connection> responseObserver) {
		super.connectNew(request, responseObserver);
	}

	@Override
	public void connect(Connection request, StreamObserver<Connection> responseObserver) {
	}
}
