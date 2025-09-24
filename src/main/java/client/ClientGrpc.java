package client;

import com.proto.calculator.CalculatorPrimeRequest;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorSumRequest;
import com.proto.calculator.CalculatorSumResponse;
import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientGrpc {
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
            case "greet_many_times":
                doGreetManyTimes(channel);
                break;
            case "sum" :
                doSum(channel);
                break;
            case "prime":
                doPrime(channel);
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

    private static void doSum(ManagedChannel channel) {
        System.out.println("Enter doSum");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        CalculatorSumResponse response = stub.sum(CalculatorSumRequest.newBuilder().setA(3).setB(10).build());

        System.out.println("Sum: " + response.getResult());
    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Enter doGreetManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        stub.greetManyTimes(GreetingRequest.newBuilder().setFirstName("Mykola").build()).forEachRemaining(response -> {
            System.out.println(response.getResult());
        });;
    }

    private static void doPrime(ManagedChannel channel) {
        System.out.println("Enter doPrime");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.prime(CalculatorPrimeRequest.newBuilder().setNumber(250).build()).forEachRemaining(response -> {
            System.out.println(response.getResult());
        });
    }
}
