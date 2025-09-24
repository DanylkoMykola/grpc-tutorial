package greeting.service;

import com.proto.greeting.GreetingServiceGrpc;

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
}
