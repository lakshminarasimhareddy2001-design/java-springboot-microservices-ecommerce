package com.eswar.paymentservice.advice;

import com.eswar.paymentservice.exception.BusinessException;
import com.eswar.paymentservice.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class PaymentRestControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex) {
        log.warn("Error from Business handler: ",ex);
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatus());

        problem.setTitle("Business Error");
        problem.setDetail(ex.getMessage());
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception occurred", ex);

        ProblemDetail problem = ProblemDetail.forStatus(
                ErrorCode.INTERNAL_ERROR.getStatus()
        );

        problem.setTitle("Internal Server Error");
        problem.setDetail(ErrorCode.INTERNAL_ERROR.getMessage());
        problem.setProperty("errorCode", ErrorCode.INTERNAL_ERROR.name());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }
}
