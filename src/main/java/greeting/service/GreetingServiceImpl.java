package greeting.service;

import com.proto.greeting.GreetingServiceGrpc;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        GreetingResponse response = GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build();
    
        for (int i = 0; i < 10; i++) {
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responObserver) {
        StringBuilder sb = new StringBuilder();
        return new StreamObserver<GreetingRequest>() {

            @Override
            public void onCompleted() {
                responObserver.onNext(GreetingResponse.newBuilder().setResult(sb.toString()).build());
                responObserver.onCompleted();
                
            }

            @Override
            public void onError(Throwable t) {
                responObserver.onError(t);
                
            }

            @Override
            public void onNext(GreetingRequest request) {
                sb.append("Hello ")
                    .append(request.getFirstName())
                    .append("!\n");
            }
            
        };
    }
    
    @Override
    public StreamObserver<GreetingRequest> greetEveryone(StreamObserver<GreetingResponse> response) {
        return new StreamObserver<GreetingRequest>() {

            @Override
            public void onCompleted() {
                response.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                response.onError(t);
            }

            @Override
            public void onNext(GreetingRequest request) {
                response.onNext(GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build());
            }
            
        };
    }

    @Override
    public void greetWithDeadline(GreetingRequest request, StreamObserver<GreetingResponse> responseObserever) {
        Context context =  Context.current();

        try {
            for (int i = 0; i < 3; ++i) {
                if (context.isCancelled()) {
                    return;
                }
                Thread.sleep(100);
            }
            responseObserever.onNext(GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build());
            responseObserever.onCompleted();
        } catch (InterruptedException e) {
            responseObserever.onError(e);
        }
       
    } 

}
