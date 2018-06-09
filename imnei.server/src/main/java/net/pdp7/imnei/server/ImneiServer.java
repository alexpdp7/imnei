package net.pdp7.imnei.server;

import java.util.UUID;

import net.pdp7.imnei.server.grpc.ImneiProto.ConnectRequest;
import net.pdp7.imnei.server.grpc.ImneiProto.ConnectResponse;

public class ImneiServer {

	public ConnectResponse handleConnectRequest(ConnectRequest connectRequest) {
		System.out.println(connectRequest);
		String connectionId = connectRequest.getConnectionId();
		if(connectionId.isEmpty()) {
			connectionId = UUID.randomUUID().toString();
		}
		return ConnectResponse.newBuilder().setConnectionResponse(connectionId).build();
	}

}
