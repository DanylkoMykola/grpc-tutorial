package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length == 0) {
            System.out.println("Need one arg to work");
            return;
        }
    
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        switch (args[0]) {
            case "greet": 
                doGreet(channel);
                break;
        
            default:
                System.out.println("Invalid arg: " + args[0]);
                break;
        }

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Mykola").build());

        System.out.println("Greeting: " + response.getResult());
    }
    
}
