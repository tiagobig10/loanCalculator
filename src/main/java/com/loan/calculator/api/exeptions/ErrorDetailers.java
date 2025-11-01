package com.loan.calculator.api.exeptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

import static com.loan.calculator.api.utils.AppConstants.TIMESTAMP;

@Data
@AllArgsConstructor
public class ErrorDetailers {
    @JsonFormat(pattern = TIMESTAMP)
    private Date timestamp;
    private int status;
    private List<ErrorMessage> erros;

}
