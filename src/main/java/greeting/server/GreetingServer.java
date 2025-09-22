package greeting.server;

import java.io.IOException;
import io.grpc.ServerBuilder;
import io.grpc.Server;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        Server server = ServerBuilder.forPort(port)
                .addService(new GreetingServerImpl())
                .build();
        
        server.start();
        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            server.shutdown();
            System.out.println("Server shut down");
        }));

        server.awaitTermination();
    }
}