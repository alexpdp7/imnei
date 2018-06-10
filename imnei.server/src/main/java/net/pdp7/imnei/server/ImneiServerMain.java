package net.pdp7.imnei.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.SelinuxContext;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.pdp7.imnei.server.grpc.impl.ImneiProtoImpl;

public class ImneiServerMain {
	Logger logger = LoggerFactory.getLogger(getClass());
	protected final int grpcPort;
	protected final int grpcWebPort;

	public ImneiServerMain(int grpcPort, int grpcWebPort) {
		this.grpcPort = grpcPort;
		this.grpcWebPort = grpcWebPort;
	}

	public void start() throws Exception {
		Server server = ServerBuilder.forPort(grpcPort).addService(new ImneiProtoImpl(new ImneiServer())).build();
		server.start();
		String serverEndPoint = "localhost:" + server.getPort();
		try (FixedHostPortGenericContainer<?> grpcWebProxy = new FixedHostPortGenericContainer<>("alexpdp7/grpcwebproxyws:latest")) {
			grpcWebProxy
				.withCommand(
						"/go/src/github.com/improbable-eng/grpc-web/go/grpcwebproxy/grpcwebproxy",
						"--backend_addr=" + serverEndPoint,
						"--server_http_tls_port=" + grpcWebPort,
						"--server_tls_cert_file=" + "/public.key",
						"--server_tls_key_file=" + "/private.key",
						"--backend_tls_noverify",
						"--use_websockets")
				.withNetworkMode("host")
				.withClasspathResourceMapping("/public.key", "/public.key", BindMode.READ_ONLY, SelinuxContext.SHARED)
				.withClasspathResourceMapping("/private.key", "/private.key", BindMode.READ_ONLY, SelinuxContext.SHARED)
				.start();
			grpcWebProxy.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("net.pdp7.imnei.server.grpcwebproxy")));
			String grpcWebEndpoint = "localhost:" + grpcWebPort;
			logger.info("GRPC on {}, GRPCWEB on {}", serverEndPoint, grpcWebEndpoint);
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws Exception {
		int grpcPort = Integer.parseInt(args[0]);
		int grpcWebPort = Integer.parseInt(args[1]);
		new ImneiServerMain(grpcPort, grpcWebPort).start();
	}
}
