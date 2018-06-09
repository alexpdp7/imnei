import queue
import sys

import grpc

from imnei_grpc import imnei_pb2
from imnei_grpc import imnei_pb2_grpc


def connect_request(connection_id):
    return imnei_pb2.ChatRequest(connectRequest=imnei_pb2.ConnectRequest(connectionId=connection_id))


channel = grpc.insecure_channel(sys.argv[1])
stub = imnei_pb2_grpc.ImneiStub(channel)

connection_id = sys.argv[2] if len(sys.argv) == 3 else None

send_queue = queue.Queue()
send_queue.put(connect_request(connection_id))

def send_queue_iter():
    while True:
        yield send_queue.get(True)

for received in stub.Chat(send_queue_iter()):
    print(received)
