package com.loan.calculator.api.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class GenerateDate {


    public LocalDate getActualMaximum(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());

        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, actualMaximum);
        return calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    public LocalDate dayOfMonth(int setDay, int setMonth, int addMonth, int setYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(setYear, setMonth, setDay);

        calendar.add(Calendar.MONTH, addMonth);

        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (setDay > actualMaximum) {
            setDay = actualMaximum;
            calendar.set(Calendar.DAY_OF_MONTH, setDay);
        }

        return calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


}
