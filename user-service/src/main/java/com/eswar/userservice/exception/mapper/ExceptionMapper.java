package com.eswar.userservice.exception.mapper;

import com.eswar.userservice.exception.BusinessException;
import com.eswar.userservice.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptionMapper {

    public BusinessException map(Throwable ex) {
        Throwable root = getRootCause(ex);

        // Handle Hibernate / JPA constraint violations
        if (root instanceof org.hibernate.exception.ConstraintViolationException cve) {
            String constraint = cve.getConstraintName();
            if ("uk_user_email".equalsIgnoreCase(constraint)) {
                return new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Email already exists");
            }
            if ("uk_user_phone".equalsIgnoreCase(constraint)) {
                return new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Phone number already exists");
            }
            return new BusinessException(ErrorCode.VALIDATION_FAILED, "Duplicate entry detected");
        }

        // Handle Spring DataIntegrityViolationException (in case DB throws directly)
        if (root instanceof DataIntegrityViolationException dive) {
            String msg = dive.getMostSpecificCause().getMessage();
            if (msg != null) {
                if (msg.contains("email")) {
                    return new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Email already exists");
                }
                if (msg.contains("phone_number")) {
                    return new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Phone number already exists");
                }
            }
            return new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        log.error("Unhandled exception", ex);
        return new BusinessException(ErrorCode.INTERNAL_ERROR);
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable result = ex;
        while (result.getCause() != null && result != result.getCause()) {
            result = result.getCause();
        }
        return result;
    }
}