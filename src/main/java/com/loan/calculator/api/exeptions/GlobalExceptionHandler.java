package com.loan.calculator.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@ControllerAdvice
@EnableWebMvc
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ErrorMessage> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String details = ((FieldError) error).getField();
            String message = error.getDefaultMessage();

            errors.add(new ErrorMessage(message, details));
        });
        ErrorDetailers errorDetails = new ErrorDetailers(new Date(), 403, errors);
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDetailers> appException(AppException ex, WebRequest w) {

        List<ErrorMessage> errors = new ArrayList<>();
        errors.add(new ErrorMessage(ex.getMessage(), w.getDescription(false)));

        ErrorDetailers errorDetailers = new ErrorDetailers(new Date(), ex.getStatus().value(), errors);

        return new ResponseEntity<>(errorDetailers, ex.getStatus());

    }




}
