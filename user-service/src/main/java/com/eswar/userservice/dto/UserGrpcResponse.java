package com.eswar.userservice.dto;

import com.eswar.userservice.constants.UserRole;

import java.util.Set;
import java.util.UUID;

public record UserGrpcResponse(
        UUID id ,
        String email ,
        String firstName ,
        String lastName,
        String password ,
        Set<UserRole> roles
) {

}
