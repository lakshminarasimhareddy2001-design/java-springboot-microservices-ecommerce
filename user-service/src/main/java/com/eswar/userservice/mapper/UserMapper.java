package com.eswar.userservice.mapper;

import com.eswar.userservice.dto.UserRequest;
import com.eswar.userservice.dto.UserResponse;
import com.eswar.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRequest request);
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "lastSeen", source = "lastSeen")
    UserResponse toResponse(UserEntity entity);
}
