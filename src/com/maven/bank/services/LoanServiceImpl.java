package com.maven.bank.services;

import com.maven.bank.dataStore.LoanRequestStatus;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.LoanRequest;
import com.maven.bank.exceptions.MavenBankLoanException;

import java.math.BigDecimal;

public class LoanServiceImpl implements  LoanService{
    @Override
    public LoanRequest approveLoanRequest(Account loanAccount) throws MavenBankLoanException {
        if (loanAccount == null){
            throw new MavenBankLoanException ( "An account is required to process loan" );
        }
        if (loanAccount.getAccountLoanRequest() == null){
            throw new MavenBankLoanException ( "No loan provided for processing" );
        }
        LoanRequest theLoanRequest = loanAccount.getAccountLoanRequest();
        theLoanRequest.setStatus (decideOnLoanRequest(loanAccount));

        return theLoanRequest;
    }

    @Override
    public LoanRequest approveLoanRequest(Customer customer, Account loanAccount) throws MavenBankLoanException {
        return approveLoanRequest(loanAccount);
    }

    public LoanRequestStatus decideOnLoanRequest(Account accountSeekingLoan) throws MavenBankLoanException {
        LoanRequestStatus decision = decideOnLoanRequestWithAccountBalance(accountSeekingLoan);
        return decision;
    }

    public LoanRequestStatus decideOnLoanRequestWithAccountBalance(Account accountSeekingLoan) throws MavenBankLoanException {
        LoanRequestStatus decision = LoanRequestStatus.PENDING;
        LoanRequest theLoanRequest = accountSeekingLoan.getAccountLoanRequest();
        BigDecimal bankAccountBalancePercentage = BigDecimal.valueOf(0.2);

        BigDecimal loanAmountApprovedAutomatically = accountSeekingLoan.getBalance ().multiply (bankAccountBalancePercentage);
        if (theLoanRequest.getLoanAmount().compareTo(loanAmountApprovedAutomatically) < BigDecimal.ZERO.intValue ()){
            decision = LoanRequestStatus.APPROVED;
        }

        return decision;
    }
}
