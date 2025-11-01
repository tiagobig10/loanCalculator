package com.loan.calculator.api.responses;

import com.loan.calculator.api.model.Competence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {
    private String loanId;
    private int loanAmount;
    private int interestRate;
    private List<Competence> competences;

}
