package com.eswar.orderservice.grpc.client;

import com.eswar.grpc.user.ProductRequest;
import com.eswar.grpc.user.ProductResponse;
import com.eswar.grpc.user.ProductServiceGrpc;
import com.eswar.grpc.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class GrpcProductServiceClient {

private final ProductServiceGrpc.ProductServiceBlockingStub stub;

    public GrpcProductServiceClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext() // disable TLS for local testing
                .build();
        this.stub = ProductServiceGrpc.newBlockingStub(channel);

    }

    public ProductResponse getProduct(UUID productId) {

        log.info("Calling Product Service via gRPC for productId: {}", productId);

        ProductRequest request = ProductRequest.newBuilder()
                .setProductId(productId.toString())
                .build();

        return stub.getProduct(request);
    }


}
