package com.maven.bank.entities;

import com.maven.bank.dataStore.LoanStatus;
import com.maven.bank.dataStore.LoanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loan {
    private BigDecimal loanAmount;
    private LoanStatus status;
    private LocalDateTime startDate;
    private int tenor;
    private LoanType typeOfLoan;
    private double interestRate;

}
