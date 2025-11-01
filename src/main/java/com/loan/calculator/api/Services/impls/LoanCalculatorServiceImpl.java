package com.loan.calculator.api.Services.impls;

import com.loan.calculator.api.Services.LoanCalculatorService;
import com.loan.calculator.api.exeptions.AppException;
import com.loan.calculator.api.model.Competence;
import com.loan.calculator.api.model.Feel;
import com.loan.calculator.api.model.Installment;
import com.loan.calculator.api.model.Principal;
import com.loan.calculator.api.payloads.RequestLoanCalculator;
import com.loan.calculator.api.responses.LoanResponse;
import com.loan.calculator.api.utils.GenerateDate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoanCalculatorServiceImpl implements LoanCalculatorService {

    @Override
    public LoanResponse generateLoanCalculator(RequestLoanCalculator requestLoanCalculator) {
        UUID resultId = UUID.randomUUID();
        int INSTALLMENTS = 120;
        int PERCENT = requestLoanCalculator.getInterestRate();
        double BASE_DAY = 360;

        LocalDate startDate = requestLoanCalculator.getStartDate();
        LocalDate firstPayment = requestLoanCalculator.getFirstPayment();
        LocalDate andDate = requestLoanCalculator.getEndDate();

        if (andDate.isBefore(startDate)) {
            throw new AppException(HttpStatus.FORBIDDEN, "A data final deve ser maior que a data inicial");
        }

        if (firstPayment.isAfter(andDate)) {
            throw new AppException(HttpStatus.FORBIDDEN, "A data de primeiro pagamento deve ser maior que a data inicial e menor que a data final");
        }

        List<Competence> competences = new ArrayList<>();

        double accumulated = 0;
        double balance = requestLoanCalculator.getLoanAmount();
        double installmentValue = requestLoanCalculator.getLoanAmount() / (double) INSTALLMENTS;

        List<Competence> generateCompetencies = generateCompetencies(startDate, firstPayment, andDate, INSTALLMENTS);

        int index = 0;
        for (Competence competence : generateCompetencies) {

            Principal principal = new Principal();
            Feel feel = new Feel();

            principal.setBalance(doubleToLong(balance));
            competence.setPrincipal(principal);
            competence.setOutstandingBalance(doubleToLong(balance));

            if (competence.getType() == 0) {
                competence.setLoanAmount(requestLoanCalculator.getLoanAmount());
            } else {
                competence.setLoanAmount(0);
            }

            if (competence.getType() == 1) {
                LocalDate start = generateCompetencies.get(index - 1).getDateCompetence();
                LocalDate end = generateCompetencies.get(index).getDateCompetence();

                double calculatorFeel = calculator(balance, calcPeriod(start, end), PERCENT, BASE_DAY);

                feel.setProvision(doubleToLong(calculatorFeel));
                feel.setAccumulated(doubleToLong(calculatorFeel));
                competence.setFeel(feel);
                accumulated += calculatorFeel;

                competence.setOutstandingBalance(doubleToLong(balance + calculatorFeel));

            }

            if (competence.getType() == 2) {
                LocalDate start = generateCompetencies.get(index - 1).getDateCompetence();
                LocalDate end = generateCompetencies.get(index).getDateCompetence();

                double calculatorFeel = calculator(balance + accumulated, calcPeriod(start, end), PERCENT, BASE_DAY);
                feel.setPaid(doubleToLong(accumulated + calculatorFeel));

                Installment installment = competence.getInstallment();
                installment.setTotal(doubleToLong(installmentValue + feel.getPaid()));

                principal.setAmortization(doubleToLong(installmentValue));

                balance -= installmentValue;
                principal.setBalance(doubleToLong(balance));
                competence.setPrincipal(principal);

                competence.setOutstandingBalance(doubleToLong(balance));

                feel.setProvision(doubleToLong(calculatorFeel));
                competence.setFeel(feel);
                accumulated = 0;
            }

            competences.add(competence);
            index++;
        }

        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setLoanId(resultId.toString());
        loanResponse.setCompetences(competences);
        loanResponse.setLoanAmount(requestLoanCalculator.getLoanAmount());
        loanResponse.setInterestRate(requestLoanCalculator.getInterestRate());
        return loanResponse;
    }


    private double calculator(double value, int days, int percent, double base_day) {
        return (Math.pow(((percent / 100.0) + 1), ((days) / base_day)) - 1) * (value + 0);
    }

    private int calcPeriod(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    private long doubleToLong(double value) {
        return Long.parseLong(String.format("%.2f", value).replace(",",""));
    }

    private List<Competence> generateCompetencies(LocalDate startDate, LocalDate firstPayment, LocalDate endDate, int INSTALLMENTS) {
        Period period = Period.between(startDate, firstPayment);
        GenerateDate generateDate = new GenerateDate();
        List<Competence> competences = new ArrayList<>();

        for (int i = 0; i < period.getMonths(); i++) {
            var dayOfMonth = generateDate.dayOfMonth(
                    startDate.getDayOfMonth(),
                    startDate.getMonthValue() - 1,
                    i,
                    startDate.getYear()
            );

            var actualMaximum = generateDate.getActualMaximum(dayOfMonth);
            competences.add(Competence.builder().dateCompetence(dayOfMonth).type(0).build());
            competences.add(Competence.builder().dateCompetence(actualMaximum).type(1).build());

        }

        for (int installment = 0; installment < INSTALLMENTS - 1; installment++) {

            var dayOfMonth = generateDate.dayOfMonth(
                    firstPayment.getDayOfMonth(),
                    firstPayment.getMonthValue() - 1,
                    installment,
                    firstPayment.getYear()
            );

            var actualMaximum = generateDate.getActualMaximum(dayOfMonth);

            competences.add(Competence.builder()
                    .dateCompetence(dayOfMonth)
                    .type(2)
                    .installment(Installment.builder().number(installment + 1)
                            .build())
                    .build());
            if (installment != INSTALLMENTS - 1) {
                competences.add(Competence.builder().dateCompetence(actualMaximum).type(1).build());
            }

        }

        competences.add(Competence.builder()
                .dateCompetence(endDate)
                .type(2)
                .installment(Installment.builder().number(INSTALLMENTS)
                        .build())
                .build());
        return competences;
    }


}
