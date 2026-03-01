package com.eswar.userservice.service;

import com.eswar.grpc.user.UserEmailRequest;
import com.eswar.grpc.user.UserIdRequest;
import com.eswar.grpc.user.UserResponse;
import com.eswar.grpc.user.UserServiceGrpc;
import com.eswar.userservice.constants.UserRole;
import com.eswar.userservice.dto.UserGrpcResponse;
import com.eswar.userservice.dto.UserResponseDto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

   private final UserService userService;

    @Override
    public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {


       UserGrpcResponse userGrpcResponse= userService.getUserByGrpcUserId(UUID.fromString(request.getId()));
        UserResponse response = UserResponse.newBuilder()
                .setId(userGrpcResponse.id().toString())
                .setEmail(userGrpcResponse.email())
                .setPassword(userGrpcResponse.password())
                .setName(userGrpcResponse.firstName()+userGrpcResponse.lastName())
                .addAllRoles(
                        userGrpcResponse.roles().stream()       // Set<UserRole>
                                .map(UserRole::name)      // convert enum to String
                                .toList()                 // to List<String>
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByEmail(UserEmailRequest request, StreamObserver<UserResponse> responseObserver) {
        // Normally fetch from DB by email
        UserResponse response = UserResponse.newBuilder()
                .setId("123")
                .setEmail(request.getEmail())
                .setName("John Doe")
                .addRoles("USER")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
