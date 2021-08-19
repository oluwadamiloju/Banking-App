package com.maven.bank.entities;

import com.maven.bank.dataStore.LoanRequestStatus;
import com.maven.bank.dataStore.LoanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanRequest {
    private BigDecimal loanAmount;
    private LoanType typeOfLoan;
    private LocalDateTime startDate;
    private LocalDateTime applyDate;
    private int tenor;
    private LoanRequestStatus Status;
    private double interestRate;

    public LoanRequest() {
    }

    public LoanRequest(BigDecimal loanAmount, int tenor, double interestRate) {
        this.loanAmount = loanAmount;
        this.tenor = tenor;
        this.interestRate = interestRate;
    }

    public BigDecimal getLoanAmount() { return loanAmount; }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public LoanType getTypeOfLoan() {
        return typeOfLoan;
    }

    public void setTypeOfLoan(LoanType typeOfLoan) {
        this.typeOfLoan = typeOfLoan;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public LoanRequestStatus getStatus() {
        return Status;
    }

    public void setStatus(LoanRequestStatus status) {
        Status = status;
    }

    public double getInterestRate(double interestRate) {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
