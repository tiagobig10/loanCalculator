package com.loan.calculator.api.controllers;

import com.loan.calculator.api.Services.LoanCalculatorService;
import com.loan.calculator.api.payloads.RequestLoanCalculator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class LoanCalculatorController {

    @Autowired
    private LoanCalculatorService loanCalculatorService;

    @PostMapping("/api/v1/loan-calculator")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> loanCalculator(@Valid @RequestBody RequestLoanCalculator requestLoanCalculator) {

        return new ResponseEntity<>(loanCalculatorService.generateLoanCalculator(requestLoanCalculator), HttpStatus.CREATED);
    }


}
