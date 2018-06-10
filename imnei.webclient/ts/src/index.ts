import {grpc} from "grpc-web-client";
import {Imnei} from "../_proto/imnei_pb_service";
import {ChatRequest, ChatResponse, ConnectRequest, ConnectResponse} from "../_proto/imnei_pb";
import { client } from "grpc-web-client/dist/client";

const host = "https://localhost:22222";

let imneiClient = grpc.client(Imnei.Chat, {
  host: host,
  transport: grpc.WebsocketTransportFactory,
});

imneiClient.start();
imneiClient.onMessage((message: ChatResponse) => {
  if(message.hasConnectresponse()) {
    let connectResponse = message.getConnectresponse();
    console.log(connectResponse!.getConnectionid());
  }
});


let chatRequest = new ChatRequest();
chatRequest.setConnectrequest(new ConnectRequest());

imneiClient.send(chatRequest);
