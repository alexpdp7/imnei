import sys

import grpc

from imnei_grpc import imnei_pb2
from imnei_grpc import imnei_pb2_grpc

channel = grpc.insecure_channel(sys.argv[1])
stub = imnei_pb2_grpc.GreeterStub(channel)
