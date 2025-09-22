package calculator.server;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorSumRequest;
import com.proto.calculator.CalculatorSumResponse;

import io.grpc.stub.StreamObserver;

public class CalculatorSeviceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase  {
    @Override
    public void sum(CalculatorSumRequest request, StreamObserver<CalculatorSumResponse> responseObserver) {
        int a = request.getA();
        int b = request.getB();

        int result = a + b;

        responseObserver.onNext(CalculatorSumResponse.newBuilder().setResult(result).build());
        responseObserver.onCompleted();
    }
}