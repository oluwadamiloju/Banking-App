package com.maven.bank.services;

import com.maven.bank.dataStore.CustomerRepo;
import com.maven.bank.dataStore.LoanRequestStatus;
import com.maven.bank.dataStore.LoanType;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.LoanRequest;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankLoanException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LoanRequestServiceImplTest {
    private LoanRequest johnLoanRequest;
    private LoanService loanService;
    private AccountServices accountService;


    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
        loanService = new LoanServiceImpl ();
        johnLoanRequest = new LoanRequest();
        johnLoanRequest.setLoanAmount (BigDecimal.valueOf (9000000));
        johnLoanRequest.setApplyDate (LocalDateTime.now());
        johnLoanRequest.getInterestRate(0.1);
        johnLoanRequest.setStatus (LoanRequestStatus.NEW);
        johnLoanRequest.setTenor (25);
        johnLoanRequest.setTypeOfLoan (LoanType.SME);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void approveLoanWithNullAccount(){
        assertThrows (MavenBankLoanException.class, () -> loanService.approveLoanRequest(null));
    }

    @Test
    void approveLoanWithNullLoan(){
        assertThrows (MavenBankLoanException.class, () -> loanService.approveLoanRequest(null));
    }
    @Test
    void approveLoanRequestWithAccountBalance() {
        try {
            Account johnCurrentAccount = accountService.findAccount(1000110002);
            assertNull(johnCurrentAccount.getAccountLoanRequest());
            johnCurrentAccount.setAccountLoanRequest(johnLoanRequest);
            LoanRequest processedLoanRequest = loanService.approveLoanRequest(johnCurrentAccount);
            assertEquals(LoanRequestStatus.APPROVED, processedLoanRequest.getStatus());
        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }
    @Test
    void approveLoanRequestWithAccountBalanceAndHighLoanRequestAmount() {
        try {
            Account johnCurrentAccount = accountService.findAccount(1000110002);
            johnLoanRequest.setLoanAmount(BigDecimal.valueOf(90000000));
            assertNull(johnCurrentAccount.getAccountLoanRequest());
            johnCurrentAccount.setAccountLoanRequest(johnLoanRequest);

            LoanRequest processedLoanRequest = loanService.approveLoanRequest(johnCurrentAccount);
            assertEquals(LoanRequestStatus.PENDING, processedLoanRequest.getStatus());
        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }
    @Test
    void approveLoanRequestWithLengthOfRelationship() {
        try {
            Account johnSavingsAccount = accountService.findAccount(1000110001);
            Optional<Customer> optionalCustomer = CustomerRepo.getCustomers().values().stream().findFirst();
            Customer john = optionalCustomer.isPresent()? optionalCustomer.get() : null;
            assertNotNull(john);
            john.setRelationshipStartAge(johnSavingsAccount.getStartDate().minusYears(2));

        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }

}
