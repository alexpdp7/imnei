import {grpc} from "grpc-web-client";
import {Imnei} from "../_proto/imnei_pb_service";
import {ChatRequest, ChatResponse, ConnectRequest, ConnectResponse, NewChatRequest, Message, SubscribeChatRequest} from "../_proto/imnei_pb";
import { client } from "grpc-web-client/dist/client";

const host = "https://localhost:22222";

let imneiClient = grpc.client(Imnei.Chat, {
  host: host,
  transport: grpc.WebsocketTransportFactory,
});

let connectionId = null;

document.getElementById("new_chat")!.addEventListener("click",() => {
  let chatRequest = new ChatRequest()
  chatRequest.setNewchatrequest(new NewChatRequest());
  imneiClient.send(chatRequest);
});

imneiClient.start();
imneiClient.onMessage((message: ChatResponse) => {
  console.log(message);
  if(message.hasConnectresponse()) {
    connectionId = message.getConnectresponse()!.getConnectionid();
    console.log("Connected with", connectionId);
    document.getElementById("connection_id")!.innerHTML = connectionId;
  }
  if(message.hasNewchatresponse()) {
    let chatId = message.getNewchatresponse()!.getChatid();
    console.log("New chat", chatId);
    let chatHeader = `
      <h2 id="header_${chatId}">${chatId}</h2>
      <ul></ul>
      <input type="text"/> <button>Send</button>
    `;
    document.getElementById("chats")!.innerHTML += chatHeader;
    let chatInput = <HTMLInputElement>document.querySelector(`#header_${chatId} ~ input`)!;
    document.querySelector(`#header_${chatId} ~ button`)!.addEventListener("click", () => {
      let chatRequest = new ChatRequest();
      let message = new Message();
      message.setChatid(chatId);
      message.setMessage(chatInput.value);
      chatRequest.setMessage(message);
      console.log("send", chatRequest);
      imneiClient.send(chatRequest);
    });
    let chatRequest = new ChatRequest();
    let subscribeChatRequest = new SubscribeChatRequest();
    subscribeChatRequest.setChatid(chatId);
    chatRequest.setSubscribechatrequest(subscribeChatRequest);
    imneiClient.send(chatRequest);
  }
  if(message.hasMessage()) {
    let chatMessage = message.getMessage()!;
    let chatId = chatMessage.getChatid()!;
    console.log(chatId);
    document.querySelector(`#header_${chatId} ~ ul`)!.innerHTML += `<li>${chatMessage.getMessage()}</li>`;
  }
});

let chatRequest = new ChatRequest();
chatRequest.setConnectrequest(new ConnectRequest());

imneiClient.send(chatRequest);
