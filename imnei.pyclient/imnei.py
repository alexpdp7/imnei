import sys

import grpc

from imnei_grpc import hello_pb2
from imnei_grpc import hello_pb2_grpc

channel = grpc.insecure_channel(sys.argv[1])
stub = hello_pb2_grpc.GreeterStub(channel)
print(stub.SayHello(hello_pb2.HelloRequest(name=sys.argv[2])))
