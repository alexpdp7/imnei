package net.pdp7.imnei.server.grpc.impl;

import io.grpc.stub.StreamObserver;
import net.pdp7.imnei.server.ImneiServer;
import net.pdp7.imnei.server.grpc.ImneiGrpc.ImneiImplBase;
import net.pdp7.imnei.server.grpc.ImneiProto;
import net.pdp7.imnei.server.grpc.ImneiProto.ChatRequest;
import net.pdp7.imnei.server.grpc.ImneiProto.ChatResponse;
import net.pdp7.imnei.server.grpc.ImneiProto.ConnectRequest;

public class ImneiProtoImpl extends ImneiImplBase {

	protected final ImneiServer imneiServer;

	public ImneiProtoImpl(ImneiServer imneiServer) {
		this.imneiServer = imneiServer;
	}

	@Override
	public StreamObserver<ChatRequest> chat(final StreamObserver<ChatResponse> responseObserver) {
		return new StreamObserver<ImneiProto.ChatRequest>() {
			@Override
			public void onNext(ImneiProto.ChatRequest request) {
				if(request.hasConnectRequest()) {
					handleConnectRequest(responseObserver, request.getConnectRequest());
				}
			}
			@Override
			public void onCompleted() {
			}
			@Override
			public void onError(Throwable arg0) {
				
			}
		};
	}

	protected void handleConnectRequest(StreamObserver<ChatResponse> responseObserver, ConnectRequest connectRequest) {
		responseObserver.onNext(ChatResponse.newBuilder().setConnectResponse(imneiServer.handleConnectRequest(connectRequest)).build());
	}
}
