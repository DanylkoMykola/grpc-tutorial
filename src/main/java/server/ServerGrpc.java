package server;

import java.io.IOException;
import java.util.List;

import calculator.server.CalculatorSeviceImpl;
import greeting.server.GreetingServiceImpl;
import io.grpc.ServerBuilder;
import io.grpc.BindableService;
import io.grpc.Server;

public class ServerGrpc {
    
    public void runGrpcServer(int port, List<BindableService> serivces) throws IOException, InterruptedException {

         ServerBuilder builder = ServerBuilder.forPort(port);

         for (BindableService serivce : serivces) {
            builder.addService(serivce);
         }
        Server server = builder.build();
        
        server.start();
        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            server.shutdown();
            System.out.println("Server shut down");
        }));

        server.awaitTermination();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        ServerGrpc server = new ServerGrpc();
        server.runGrpcServer(port, List.of(new GreetingServiceImpl(), new CalculatorSeviceImpl()));
    }
}
