package net.pdp7.imnei.server.grpc.impl;

import io.grpc.stub.StreamObserver;
import net.pdp7.imnei.server.ImneiServer;
import net.pdp7.imnei.server.grpc.ImneiGrpc.ImneiImplBase;
import net.pdp7.imnei.server.grpc.ImneiProto;
import net.pdp7.imnei.server.grpc.ImneiProto.ChatRequest;
import net.pdp7.imnei.server.grpc.ImneiProto.ChatResponse;
import net.pdp7.imnei.server.grpc.ImneiProto.Message;

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
					responseObserver.onNext(ChatResponse.newBuilder().setConnectResponse(imneiServer.handleConnectRequest(request.getConnectRequest().getConnectionId())).build());
				}
				if(request.hasNewChatRequest()) {
					responseObserver.onNext(ChatResponse.newBuilder().setNewChatResponse(imneiServer.handleNewChatRequest(request.getNewChatRequest().getConnectionId())).build());
				}
				if(request.hasMessage()) {
					Message message = request.getMessage();
					imneiServer.handleSentMessage(message.getChatId(), message.getMessage());
				}
				if(request.hasSubscribeChatRequest()) {
					imneiServer.subscribeChat(request.getSubscribeChatRequest().getChatId(), (String message) -> responseObserver.onNext(ChatResponse.newBuilder().setMessage(Message.newBuilder().setMessage(message).build()).build()));
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
}
