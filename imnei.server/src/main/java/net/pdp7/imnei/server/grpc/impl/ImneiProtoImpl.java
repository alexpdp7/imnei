package net.pdp7.imnei.server.grpc.impl;

import io.grpc.stub.StreamObserver;
import net.pdp7.imnei.server.ImneiMessenger;
import net.pdp7.imnei.server.ImneiMessenger.ImneiMessengerReceiver;
import net.pdp7.imnei.server.ImneiServer;
import net.pdp7.imnei.server.grpc.ImneiGrpc.ImneiImplBase;
import net.pdp7.imnei.server.grpc.ImneiProto;
import net.pdp7.imnei.server.grpc.ImneiProto.ChatRequest;
import net.pdp7.imnei.server.grpc.ImneiProto.ChatResponse;
import net.pdp7.imnei.server.grpc.ImneiProto.ConnectRequest;
import net.pdp7.imnei.server.grpc.ImneiProto.Message;
import net.pdp7.imnei.server.grpc.ImneiProto.NewChatRequest;
import net.pdp7.imnei.server.grpc.ImneiProto.NewChatResponse;

public class ImneiProtoImpl extends ImneiImplBase {

	protected final ImneiServer imneiServer;
	protected final ImneiMessenger imneiMessenger;

	public ImneiProtoImpl(ImneiServer imneiServer, ImneiMessenger imneiMessenger) {
		this.imneiServer = imneiServer;
		this.imneiMessenger = imneiMessenger;
	}

	@Override
	public StreamObserver<ChatRequest> chat(final StreamObserver<ChatResponse> responseObserver) {
		ConnectionThread connectionThread = new ConnectionThread();
		new Thread(connectionThread).start();
		return new StreamObserver<ImneiProto.ChatRequest>() {
			@Override
			public void onNext(ImneiProto.ChatRequest request) {
				if(request.hasConnectRequest()) {
					handleConnectRequest(responseObserver, request.getConnectRequest());
				}
				if(request.hasNewChatRequest()) {
					handleNewChatRequest(responseObserver, request.getNewChatRequest(), connectionThread);
				}
				if(request.hasMessage()) {
					handleSentMessage(responseObserver, request.getMessage());
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

	protected void handleSentMessage(StreamObserver<ChatResponse> responseObserver, Message message) {
		imneiServer.handleSentMessage(message.getChatId(), message.getMessage());
	}

	protected void handleNewChatRequest(StreamObserver<ChatResponse> responseObserver, NewChatRequest newChatRequest, ConnectionThread connectionThread) {
		NewChatResponse newChatResponse = imneiServer.handleNewChatRequest(newChatRequest.getConnectionId());
		connectionThread.subscribe(newChatResponse.getChatId());
		responseObserver.onNext(ChatResponse.newBuilder().setNewChatResponse(newChatResponse).build());
	}

	protected void handleConnectRequest(StreamObserver<ChatResponse> responseObserver, ConnectRequest connectRequest) {
		responseObserver.onNext(ChatResponse.newBuilder().setConnectResponse(imneiServer.handleConnectRequest(connectRequest.getConnectionId())).build());
	}

	protected class ConnectionThread implements Runnable {
		protected final ImneiMessengerReceiver receiver;
		protected ConnectionThread() {
			receiver = imneiMessenger.createReceiver();
		}
		public void subscribe(String chatId) {
			receiver.subscribe(chatId);
		}
		@Override
		public void run() {
			receiver.start();
		}
	}
}
