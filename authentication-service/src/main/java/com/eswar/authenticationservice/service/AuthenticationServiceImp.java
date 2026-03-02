package com.eswar.authenticationservice.service;

import com.eswar.authenticationservice.dto.AccessTokenResponseDto;
import com.eswar.authenticationservice.dto.AuthenticationResponseDto;
import com.eswar.authenticationservice.dto.LoginRequestDto;
import com.eswar.authenticationservice.dto.RefreshTokenRequestDto;
import com.eswar.grpc.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImp implements IAuthenticationService {

//    private final JwtService jwtService;

    private final GrpcUserServiceClient client;

    @Override
    public AuthenticationResponseDto login(LoginRequestDto request) {

        if(request==null && request.email() == null)
            throw new RuntimeException("Invalid Request");

        assert request != null;

         UserResponse userResponse= client.getUserByEmail(request.email());

         log.info("we are getting data from grpc request {}",userResponse.getEmail());

        return null;
    }

    @Override
    public AccessTokenResponseDto refresh(RefreshTokenRequestDto request) {

        return null;
    }
}
