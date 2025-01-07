package com.muse.RhyFeel.handler;

import com.muse.RhyFeel.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityExceptionHandler {

    // 401 Unauthorized - 인증 실패
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return buildResponseEntity("401", "Authentication Failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 403 Forbidden - 권한 부족
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponseEntity("403", "Access Denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // 401 Unauthorized - 인증 정보 누락
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFound(AuthenticationCredentialsNotFoundException ex) {
        return buildResponseEntity("401", "Authentication Required: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(String status, String message, HttpStatus httpStatus) {
        ErrorResponse errorResponse = new ErrorResponse(status, message);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
