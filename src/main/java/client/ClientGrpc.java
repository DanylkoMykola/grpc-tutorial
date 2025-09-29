package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.proto.calculator.CalculatorAvgRequest;
import com.proto.calculator.CalculatorAvgResponse;
import com.proto.calculator.CalculatorPrimeRequest;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorSumRequest;
import com.proto.calculator.CalculatorSumResponse;
import com.proto.calculator.SqrtRequest;
import com.proto.calculator.SqrtResponse;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

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
            case "long_greet":
                doLongGreet(channel);
                break;
            case  "greet_everyone":
                doGreetEveryone(channel);
                break;
            case "sum" :
                doSum(channel);
                break;
            case "prime":
                doPrime(channel);
                break;
            case "avg":
                doAvg(channel);
                break;
            case "sqrt":
                doSqrt(channel);
                break;
            case "dead_line" :
                doGreetWithDeadline(channel);
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

    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doLongGreet");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        List<String> names = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Collections.addAll(names, "Mykola", "Tom", "Psina");

        StreamObserver<GreetingRequest> streamObserver = stub.longGreet(new StreamObserver<GreetingResponse>() {

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                
            }

            @Override
            public void onNext(GreetingResponse response) {
                System.out.println(response.getResult());
            }
            
        });
        for (String name : names) {
            streamObserver.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        }
        streamObserver.onCompleted();
        countDownLatch.await(3, TimeUnit.SECONDS);
    }

    public static void doAvg(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doAvg");
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<CalculatorAvgRequest> requestObserver = stub.avg(new StreamObserver<CalculatorAvgResponse>() {

            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                
            }

            @Override
            public void onNext(CalculatorAvgResponse response) {
                System.out.println("Result: " + response.getResult());
            }
        });
        numbers.stream().forEach(n -> requestObserver.onNext(CalculatorAvgRequest.newBuilder().setNumber(n).build()));
        requestObserver.onCompleted();
        latch.await(5, TimeUnit.SECONDS);
    }

    private static void doGreetEveryone(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doGreetEveryone");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetingRequest> requestObserver = stub.greetEveryone(new StreamObserver<GreetingResponse>() {

            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onNext(GreetingResponse response) {
                System.out.println(response.getResult());
            }
            
        });
        Arrays.asList("Mykola", "Tom", "Pes")
            .forEach(name -> requestObserver.onNext(GreetingRequest.newBuilder().setFirstName(name).build()));

        requestObserver.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doSqrt(ManagedChannel channel) {
        System.out.println("Enter doSqrt");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNumber(25).build());

        System.out.println("Sqrt 25 = " + response.getResult());

        try {
            response = stub.sqrt(SqrtRequest.newBuilder().setNumber(-5).build());
            System.out.println("Sqrt -1 = " +response.getResult());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void doGreetWithDeadline(ManagedChannel channel) {
        System.out.println("Enter doGreetWithDeadline");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        GreetingResponse response = stub.withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                                        .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Mykola").build());
        
        System.out.println("Greeting within deadline: " + response.getResult());

        try {
            response = stub.withDeadline(Deadline.after(100, TimeUnit.SECONDS))
                        .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Mykola").build());
            System.out.println("Greeting Deadline exceeded");
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded");
            }
            else {
                System.out.println("Get exception");
                e.printStackTrace();
            }
        }
    }
}
