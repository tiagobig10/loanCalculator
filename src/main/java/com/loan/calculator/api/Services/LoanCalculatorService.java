package com.loan.calculator.api.Services;

import com.loan.calculator.api.payloads.RequestLoanCalculator;
import com.loan.calculator.api.responses.LoanResponse;

public interface LoanCalculatorService {
     LoanResponse generateLoanCalculator(RequestLoanCalculator requestLoanCalculator);

}
