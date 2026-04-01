package com.eswar.userservice.validation;

import com.eswar.userservice.dto.UserRequestDto;
import com.eswar.userservice.exception.BusinessException;
import com.eswar.userservice.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateCreateUser(UserRequestDto request) {

        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Email is required");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Password is required");
        }
    }
}
