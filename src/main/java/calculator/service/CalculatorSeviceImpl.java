package calculator.service;

import java.util.ArrayList;
import java.util.List;

import com.proto.calculator.CalculatorAvgRequest;
import com.proto.calculator.CalculatorAvgResponse;
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

    @Override
    public StreamObserver<CalculatorAvgRequest> avg(StreamObserver<CalculatorAvgResponse> responseObserver) {
        List<Integer> numbers = new ArrayList<>();

        return new StreamObserver<CalculatorAvgRequest>() {

            @Override
            public void onCompleted() {
                double result = numbers.stream().mapToInt(Integer::intValue).average().getAsDouble();
                responseObserver.onNext(CalculatorAvgResponse.newBuilder().setResult(result).build());  
                responseObserver.onCompleted(); 
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);    
            }

            @Override
            public void onNext(CalculatorAvgRequest request) {
                int number = request.getNumber();
                numbers.add(number);
            }
        };
    }
}