package com.loan.calculator.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.loan.calculator.api.utils.AppConstants.DATETIME;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Competence {

    @JsonFormat(pattern = DATETIME)
    private LocalDate dateCompetence;
    private long outstandingBalance;
    private long loanAmount;
    private CompetenceType type;
    private Installment installment;
    private Principal principal;
    private Feel feel;


}
