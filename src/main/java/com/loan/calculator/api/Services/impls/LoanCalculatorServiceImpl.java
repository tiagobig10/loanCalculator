package com.loan.calculator.api.Services.impls;

import com.loan.calculator.api.Services.LoanCalculatorService;
import com.loan.calculator.api.exeptions.AppException;
import com.loan.calculator.api.model.*;
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

import static com.loan.calculator.api.utils.AppConstants.*;

@Service
public class LoanCalculatorServiceImpl implements LoanCalculatorService {
    private final int DEFAULT_INSTALLMENTS = 120;
    private final double DAYS_IN_YEAR_FOR_CALC = 360;
    private int interestRatePercent = 0;

    @Override
    public LoanResponse generateLoanCalculator(RequestLoanCalculator requestLoanCalculator) {
        final UUID resultId = UUID.randomUUID();
        final LocalDate startDate = requestLoanCalculator.getStartDate();
        final LocalDate firstPaymentDate = requestLoanCalculator.getFirstPayment();
        final LocalDate endDate = requestLoanCalculator.getEndDate();

        this.interestRatePercent = requestLoanCalculator.getInterestRate();

        validateDates(startDate, firstPaymentDate, endDate);

        double accumulatedInterest = 0;
        double balance = requestLoanCalculator.getLoanAmount();
        double principalInstallmentValue = requestLoanCalculator.getLoanAmount() / (double) this.DEFAULT_INSTALLMENTS;

        List<Competence> generateCompetencies = generateCompetencies(startDate, firstPaymentDate, endDate, DEFAULT_INSTALLMENTS);
        List<Competence> competences = new ArrayList<>();

        int competenceIndex = 0;
        for (Competence competence : generateCompetencies) {

            Principal principal = new Principal();
            Feel feel = new Feel();

            principal.setBalance(doubleToLong(balance));
            competence.setPrincipal(principal);


            competence.setOutstandingBalance(doubleToLong(balance));
            if (competenceIndex > 0 ) {
                LocalDate periodEnd = generateCompetencies.get(competenceIndex).getDateCompetence();
                double calculatorFeel = calculateInterest(balance, calcPeriod(startDate, periodEnd));
                competence.setOutstandingBalance(doubleToLong(balance + calculatorFeel));
            }

            if (competence.getType() == CompetenceType.INITIAL_COMPETENCE) {
                competence.setLoanAmount(requestLoanCalculator.getLoanAmount());
            } else {
                competence.setLoanAmount(0);
            }

            if (competence.getType() == CompetenceType.FEEL_PROVISION) {
                LocalDate periodStart = generateCompetencies.get(competenceIndex - 1).getDateCompetence();
                LocalDate periodEnd = generateCompetencies.get(competenceIndex).getDateCompetence();

                double calculatorFeel = calculateInterest(balance, calcPeriod(periodStart, periodEnd));
                feel.setProvision(doubleToLong(calculatorFeel));
                feel.setAccumulated(doubleToLong(calculatorFeel));

                competence.setFeel(feel);
                accumulatedInterest += calculatorFeel;
                competence.setOutstandingBalance(doubleToLong(balance + calculatorFeel));
            }


            if (competence.getType() == CompetenceType.INSTALLMENT_PAYMENT) {
                LocalDate periodStart = generateCompetencies.get(competenceIndex - 1).getDateCompetence();
                LocalDate periodEnd = generateCompetencies.get(competenceIndex).getDateCompetence();

                double interestFromLastPeriod = calculateInterest(balance + accumulatedInterest, calcPeriod(periodStart, periodEnd));
                feel.setPaid(doubleToLong(accumulatedInterest + interestFromLastPeriod));

                Installment installment = competence.getInstallment();
                installment.setTotal(doubleToLong(principalInstallmentValue + feel.getPaid()));

                principal.setAmortization(doubleToLong(principalInstallmentValue));

                balance -= principalInstallmentValue;
                principal.setBalance(doubleToLong(balance));
                competence.setPrincipal(principal);

                competence.setOutstandingBalance(doubleToLong(balance));

                feel.setProvision(doubleToLong(interestFromLastPeriod));
                competence.setFeel(feel);
                accumulatedInterest = 0;
            }

            competences.add(competence);
            competenceIndex++;
        }

        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setLoanId(resultId.toString());
        loanResponse.setCompetences(competences);
        loanResponse.setLoanAmount(requestLoanCalculator.getLoanAmount());
        loanResponse.setInterestRate(requestLoanCalculator.getInterestRate());
        return loanResponse;
    }

    private void validateDates(LocalDate startDate, LocalDate firstPaymentDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new AppException(HttpStatus.FORBIDDEN, MESSAGE_EXCEPTION_DATE_END);
        }

        if (!firstPaymentDate.isAfter(startDate)) {
            throw new AppException(HttpStatus.FORBIDDEN, MESSAGE_EXCEPTION_DATE_START);
        }

        if (!firstPaymentDate.isBefore(endDate)) {
            throw new AppException(HttpStatus.FORBIDDEN, MESSAGE_EXCEPTION_DATE_FIRST);
        }
    }

    private double calculateInterest(double value, int days) {
        return (Math.pow(((this.interestRatePercent / 100.0) + 1), ((days) / this.DAYS_IN_YEAR_FOR_CALC)) - 1) * (value + 0);
    }

    private int calcPeriod(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    private long doubleToLong(double value) {
        return Math.round(value);
    }

    private List<Competence> generateCompetencies(LocalDate startDate, LocalDate firstPayment, LocalDate endDate, int installments) {
        Period period = Period.between(startDate, firstPayment);
        GenerateDate generateDate = new GenerateDate();
        List<Competence> competences = new ArrayList<>();

        int periodValue = period.getMonths();

        if (periodValue == 0) {
            var dayOfMonth = generateDate.dayOfMonth(
                    startDate.getDayOfMonth(),
                    startDate.getMonthValue() - 1,
                    0,
                    startDate.getYear()
            );
            competences.add(Competence.builder().dateCompetence(dayOfMonth).type(CompetenceType.INITIAL_COMPETENCE).build());
        }

        for (int i = 0; i < periodValue; i++) {
            var dayOfMonth = generateDate.dayOfMonth(
                    startDate.getDayOfMonth(),
                    startDate.getMonthValue() - 1,
                    i,
                    startDate.getYear()
            );

            var actualMaximum = generateDate.getActualMaximum(dayOfMonth);
            competences.add(Competence.builder().dateCompetence(dayOfMonth).type(CompetenceType.INITIAL_COMPETENCE).build());
            competences.add(Competence.builder().dateCompetence(actualMaximum).type(CompetenceType.FEEL_PROVISION).build());

        }

        for (int installment = 0; installment < installments - 1; installment++) {

            var dayOfMonth = generateDate.dayOfMonth(
                    firstPayment.getDayOfMonth(),
                    firstPayment.getMonthValue() - 1,
                    installment,
                    firstPayment.getYear()
            );

            var actualMaximum = generateDate.getActualMaximum(dayOfMonth);

            competences.add(Competence.builder()
                    .dateCompetence(dayOfMonth)
                    .type(CompetenceType.INSTALLMENT_PAYMENT)
                    .installment(Installment.builder().number(installment + 1)
                            .build())
                    .build());
            if (installment != installments - 1) {
                competences.add(Competence.builder().dateCompetence(actualMaximum).type(CompetenceType.FEEL_PROVISION).build());
            }

        }

        competences.add(Competence.builder()
                .dateCompetence(endDate)
                .type(CompetenceType.INSTALLMENT_PAYMENT)
                .installment(Installment.builder().number(installments)
                        .build())
                .build());
        return competences;
    }

}
