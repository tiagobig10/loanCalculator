package com.loan.calculator.api.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLoanCalculator {

    @NotNull(message = "Data de inicio é obrigatório")
    private LocalDate startDate;

    @NotNull(message = "Data do final é obrigatório")
    private LocalDate endDate;

    @NotNull(message = "Data do primeiro pagamento é obrigatório")
    private LocalDate firstPayment;

    @NotNull(message = "Valor do empréstimo é obrigatório")
    private Integer loanAmount;

    @NotNull(message = "Taxa de juros é obrigatório")
    private Integer interestRate;

}
