package net.pdp7.imnei.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.ConcurrentSet;
import net.pdp7.imnei.server.grpc.ImneiProto.ConnectResponse;
import net.pdp7.imnei.server.grpc.ImneiProto.NewChatResponse;

public class ImneiServer {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Map<String, Set<ChatListener>> chatListeners = new ConcurrentHashMap<>();

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
		chatListeners.put(newChatId, new HashSet<>());
		logger.debug("create new chat {}", newChatId);
		return NewChatResponse.newBuilder().setChatId(newChatId).build();
	}

	public void handleSentMessage(String chatId, String message) {
		logger.debug("new message in {}: {}", chatId, message);
		for(ChatListener listener : chatListeners.get(chatId)) {
			listener.newMessage(message);
		}
	}

	public void subscribeChat(String chatId, ChatListener listener) {
		chatListeners.get(chatId).add(listener);
	}

	public interface ChatListener {
		public void newMessage(String message);
	}
}
