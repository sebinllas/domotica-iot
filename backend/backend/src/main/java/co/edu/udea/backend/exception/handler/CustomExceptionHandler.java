package co.edu.udea.backend.exception.handler;

import co.edu.udea.backend.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {



    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity handleConstraintValidationException(ConstraintViolationException ex, HttpServletRequest request) {
        System.out.println(request.getRequestURL());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        System.out.println(request.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


}
