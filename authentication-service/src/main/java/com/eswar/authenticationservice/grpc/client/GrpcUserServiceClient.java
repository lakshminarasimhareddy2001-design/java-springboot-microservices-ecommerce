package com.eswar.authenticationservice.grpc.client;

import com.eswar.grpc.user.UserEmailRequest;
import com.eswar.grpc.user.UserIdRequest;
import com.eswar.grpc.user.UserResponse;
import com.eswar.grpc.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class GrpcUserServiceClient {

    private final UserServiceGrpc.UserServiceBlockingStub stub;


    public GrpcUserServiceClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext() // disable TLS for local testing
                .build();
        stub = UserServiceGrpc.newBlockingStub(channel);
    }

    public UserResponse getUserByEmail(String email) {
        UserEmailRequest request = UserEmailRequest.newBuilder()
                .setEmail(email)
                .build();
        return stub.getUserByEmail(request);
    }

    public UserResponse getUserById(String id) {
        UserIdRequest request = UserIdRequest.newBuilder()
                .setId(id)
                .build();
        return stub.getUserById(request);
    }
}
