package com.loan.calculator.api.exeptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AppException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    private HttpStatus status;
    private String message;
}
