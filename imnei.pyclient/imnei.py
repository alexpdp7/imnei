import queue
import sys
import threading

import grpc

from imnei_grpc import imnei_pb2
from imnei_grpc import imnei_pb2_grpc


def connect_request(connection_id):
    return imnei_pb2.ChatRequest(connectRequest=imnei_pb2.ConnectRequest(connectionId=connection_id))


class ImneiClient:
    def __init__(self, server, connection_id):
        self.server = server
        self.connection_id = connection_id
        self.send_queue = queue.Queue()

    def start(self):
        self._connect_to_server()
        self._send_connect_request()
        threading.Thread(target=self._thread).start()

    def _thread(self):
        for received in self.stub.Chat(self._send_queue_iter()):
            print(received)

    def _connect_to_server(self):
        self.channel = grpc.insecure_channel(self.server)
        self.stub = imnei_pb2_grpc.ImneiStub(self.channel)

    def _send_connect_request(self):
        self.send_queue.put(connect_request(self.connection_id))

    def _send_queue_iter(self):
        while True:
            yield self.send_queue.get(True)


def main():
    server = sys.argv[1]
    connection_id = sys.argv[2] if len(sys.argv) == 3 else None
    client = ImneiClient(server, connection_id)
    client.start()


if __name__ == '__main__':
    main()
