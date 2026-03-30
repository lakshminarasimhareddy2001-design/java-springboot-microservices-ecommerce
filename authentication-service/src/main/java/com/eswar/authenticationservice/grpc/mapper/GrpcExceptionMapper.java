package com.eswar.authenticationservice.grpc.mapper;

import com.eswar.authenticationservice.exception.BusinessException;
import com.eswar.authenticationservice.exception.ErrorCode;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class GrpcExceptionMapper {


    private GrpcExceptionMapper(){}
    public static BusinessException map(StatusRuntimeException ex) {

        Status.Code code = ex.getStatus().getCode();

        return switch (code) {

            case NOT_FOUND ->
                    new BusinessException(ErrorCode.USER_NOT_FOUND);

            case UNAVAILABLE ->
                    new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);

            case DEADLINE_EXCEEDED ->
                    new BusinessException(ErrorCode.TIMEOUT);

            default ->
                    new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR);
        };
    }
}