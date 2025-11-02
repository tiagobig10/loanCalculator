package com.loan.calculator.api.Services.impls;

import com.loan.calculator.api.exeptions.AppException;
import com.loan.calculator.api.payloads.RequestLoanCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanCalculatorServiceImplTest {

    private LoanCalculatorServiceImpl service;
    @BeforeEach
    void setUp() {
        this.service = new LoanCalculatorServiceImpl();
    }

    @Test
    @DisplayName("Deve calcular os juros corretamente para 30 dias a 10% a.a.")
    void calculateInterest_ShouldCalculateCorrectlyFor30Days() throws Exception {

        Method calculateInterestMethod = LoanCalculatorServiceImpl.class.getDeclaredMethod(
                "calculateInterest", double.class, int.class
        );
        calculateInterestMethod.setAccessible(true);

        // Inicializa a taxa de juros
        service.generateLoanCalculator(
                new RequestLoanCalculator(
                        LocalDate.now(),
                        LocalDate.now().plusMonths(2),
                        LocalDate.now().plusMonths(1),
                        100000L,
                        10
                )
        );

        double principalValue = 100000.0;
        int days = 30;
        double expectedInterest;

        double base = (10.0 / 100.0) + 1;
        double exponent = days / 360.0;
        expectedInterest = (Math.pow(base, exponent) - 1) * principalValue;

        // Invoca o método privado
        Double result = (Double) calculateInterestMethod.invoke(service, principalValue, days);

        assertEquals(expectedInterest, result, 0.0001, "O cálculo de juros para 30 dias está incorreto.");

    }


    @Test
    @DisplayName("Deve lançar exceção quando a data final não for depois da data inicial")
    void validateDates_ShouldThrowExceptionWhenEndDateIsNotAfterStartDate() throws Exception {
        // RANGE
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 1);
        LocalDate firstDate = LocalDate.of(2024, 1, 1);

        Method validateDatesMethod = LoanCalculatorServiceImpl.class.getDeclaredMethod(
                "validateDates", LocalDate.class, LocalDate.class, LocalDate.class
        );
        validateDatesMethod.setAccessible(true);

        assertThrows(
                InvocationTargetException.class,
                () -> {
                    validateDatesMethod.invoke(service, startDate, firstDate, endDate);
                },
                "Deveria lançar Exception quando a data final for igual à inicial"
        );

    }



}