package net.pdp7.imnei.server;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pdp7.imnei.server.grpc.ImneiProto.ConnectResponse;
import net.pdp7.imnei.server.grpc.ImneiProto.NewChatResponse;

public class ImneiServer {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public ConnectResponse handleConnectRequest(String  connectionId) {
		logger.debug("connect request for {}", connectionId);
		if(connectionId.isEmpty()) {
			connectionId = UUID.randomUUID().toString();
			logger.debug("set to {}", connectionId);
		}
		return ConnectResponse.newBuilder().setConnectionId(connectionId).build();
	}

	public NewChatResponse handleNewChatRequest(String connectionId) {
		logger.debug("new chat request from {}", connectionId);
		String newChatId = UUID.randomUUID().toString();
		logger.debug("create new chat {}", newChatId);
		return NewChatResponse.newBuilder().setChatId(newChatId).build();
	}

	public void handleSentMessage(String chatId, String message) {
		logger.debug("new message in {}: {}", chatId, message);
		imneiMessenger.sendMessage(chatId, message);
	}

}
