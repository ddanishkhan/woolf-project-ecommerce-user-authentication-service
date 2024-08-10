package com.ecommerce_user_authentication.exception;

import com.ecommerce_user_authentication.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleRoleAlreadyExistsException(RoleAlreadyExistsException ex) {
        ErrorResponse response = new ErrorResponse("Role already exists: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({InvalidLoginCredentialsException.class, InvalidTokenException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(Exception ex) {
        var response = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({UserAlreadyExistsException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleCustomExceptions(Exception ex) {
        var response = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
