package calculator.service;

import com.proto.calculator.CalculatorPrimeRequest;
import com.proto.calculator.CalculatorPrimeResponse;
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

    @Override
    public void prime(CalculatorPrimeRequest request, StreamObserver<CalculatorPrimeResponse> responseObserver) {
        int number = request.getNumber();
        int k = 2;

        while (number > 1) {
            if (number%k == 0) {
                responseObserver.onNext(CalculatorPrimeResponse.newBuilder().setResult(k).build());
                number = number/k;
            }
            else {
                k = k + 1;
            }
        }
        responseObserver.onCompleted();
    }
}