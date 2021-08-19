package com.maven.bank.services;

import com.maven.bank.dataStore.LoanRequestStatus;
import com.maven.bank.dataStore.LoanType;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.dataStore.CustomerRepo;
import com.maven.bank.entities.LoanRequest;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientBankException;
import com.maven.bank.exceptions.MavenBankTransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceImplTest {
    private AccountServices accountService;
    private Customer joy;
    private Customer janet;
    private Account joyAccount;
    private Account janetAccount;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
        joy = new Customer();
        joy.setBvn(BankService.generateBvn());
        joy.getEmail("john@doe.com");
        joy.setFirstName("joy");
        joy.setSurname("Danladi");
        joy.setPhone("2345678901");

        janet = new Customer();
        janet.setBvn(BankService.generateBvn());
        janet.getEmail("jane@blackie.com");
        janet.setFirstName("janet");
        janet.setSurname("blackie");
        janet.setPhone("90876543211");
    }

    @AfterEach
    void tearDown()  {
        BankService.tearDown();
        CustomerRepo.tearDown();
    }

    @Test
    void openSavingsAccount() {
        assertTrue(joy.getAccounts().isEmpty());
        assertEquals(1000110003, BankService.getCurrentAccountNumber());
        try {
            long newAccountNumber = accountService.openSavingsAccount(joy);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertFalse(joy.getAccounts().isEmpty());
            System.out.println(joy.getAccounts().get(0));
            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());
        } catch (MavenBankException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    void openAccountWithNoCustomer() {
        assertThrows(MavenBankException.class, () -> accountService.openAccount(null, AccountType.SAVINGSACCOUNT));
    }

//    @Test
//    void openAccountWithNoAccountType() {
//        assertThrows(MavenBankException.class, () -> accountService.openAccount(joy, null));
//    }

    @Test
    void openTheSameTypeOfAccountForTheSameCustomer() {
        Optional<Customer> johnOptional = CustomerRepo.getCustomers().values().stream().findFirst();
        Customer john = (johnOptional.isEmpty()) ? null : johnOptional.get();
        assertEquals(1000110003, BankService.getCurrentAccountNumber());
        assertNotNull(john);
        assertNotNull(john.getAccounts());
        assertFalse(john.getAccounts().isEmpty());
        assertEquals(AccountType.SAVINGSACCOUNT.toString(), john.getAccounts().get(0).getClass().getSimpleName().toUpperCase());

        assertThrows(MavenBankException.class, () -> accountService.openAccount(john, AccountType.SAVINGSACCOUNT));
        assertEquals(1000110003, BankService.getCurrentAccountNumber());
        assertEquals(2, john.getAccounts().size());
    }

    @Test
    void openAccountForCurrentAccount() {
        assertTrue(joy.getAccounts().isEmpty());
        assertEquals(1000110003, BankService.getCurrentAccountNumber());

        try {
            long newAccountNumber = accountService.openCurrentAccount(joy);
            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertFalse(joy.getAccounts().isEmpty());
            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());
        } catch (MavenBankException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    void openDifferentTypeOfAccountForTheSameCustomer() {
        try {
            long newAccountNumber = accountService.openSavingsAccount(joy);

            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertEquals(1, joy.getAccounts().size());
            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());
            newAccountNumber = accountService.openCurrentAccount(joy);
            assertEquals(1000110005, BankService.getCurrentAccountNumber());
            assertEquals(2, joy.getAccounts().size());
            assertEquals(newAccountNumber, joy.getAccounts().get(1).getAccountNumber());

        } catch (MavenBankException e) {
            e.printStackTrace();
        }

    }

    @Test
    void openSavingsAccountForANewCustomer() {

        assertTrue(joy.getAccounts().isEmpty());
        assertEquals(1000110003, BankService.getCurrentAccountNumber());

        try {
            long newAccountNumber = accountService.openSavingsAccount(joy);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertFalse(joy.getAccounts().isEmpty());
            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());

            newAccountNumber = accountService.openCurrentAccount(janet);
            assertEquals(4, CustomerRepo.getCustomers().size());
            assertEquals(1000110005, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(janet.getBvn()));
            assertFalse(janet.getAccounts().isEmpty());
            assertEquals(1, janet.getAccounts().size());
            assertEquals(newAccountNumber, janet.getAccounts().get(0).getAccountNumber());
            assertEquals(1, joy.getAccounts().size());
        } catch (MavenBankException ex) {
            ex.printStackTrace();
        }

    }
    @Test
    void findAccount() {
        try {
            Account johnCurrentAccount = accountService.findAccount(1000110002);
            assertNotNull(johnCurrentAccount);
            assertEquals(1000110002, johnCurrentAccount.getAccountNumber());
            assertEquals(AccountType.CURRENTACCOUNT.toString(), johnCurrentAccount.getClass().getSimpleName().toUpperCase());
        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    void openAccountForSavingsAccount() {
//        assertTrue(joy.getAccounts().isEmpty());
//        assertEquals(1000110003, BankService.getCurrentAccountNumber());
//        assertFalse(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
//        try {
//            long newAccount = accountService.openSavingsAccount(joy);
//            assertFalse(CustomerRepo.getCustomers().isEmpty());
//            assertEquals(1000110004, BankService.getCurrentAccountNumber());
//            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
//            assertFalse(joy.getAccounts().isEmpty());
//            assertEquals(newAccount, joy.getAccounts().get(0).getAccountNumber());
//
//        } catch (MavenBankException e) {
//            e.printStackTrace();
//        }
//    }
    @Test
    void deposit() {
        try {
            Account johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(BigDecimal.ZERO, johnSavingsAccount.getBalance());
            BigDecimal accountBalance = accountService.deposit(new BigDecimal(50000), 1000110001);
            johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(accountBalance, johnSavingsAccount.getBalance());
            assertNotNull(johnSavingsAccount.getBalance());

        } catch (MavenBankTransactionException e) {
            e.printStackTrace();
        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }
    @Test
    void depositNegativeAmount() {
        assertThrows(MavenBankTransactionException.class, () -> accountService.deposit(new BigDecimal(-50000), 1000110001));
    }
    @Test
    void depositVeryLargeAmount() {
        try {
            Account johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(BigDecimal.ZERO, johnSavingsAccount.getBalance());
            BigDecimal depositAmount = new BigDecimal("100000000000000000000");
            BigDecimal accountBalance = accountService.deposit(depositAmount, 1000110001);
            johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(depositAmount, johnSavingsAccount.getBalance());
            assertNotNull(johnSavingsAccount.getBalance());

        } catch (MavenBankTransactionException e) {
            e.printStackTrace();
        } catch (MavenBankException e) {
            e.printStackTrace();
        }

    }
    @Test
    void depositToInvalidAccountNumber(){
        assertThrows (MavenBankException.class,
                () -> accountService.deposit (new BigDecimal (-5000), 1000110001));
    }
    @Test
    void withdraw() {
        try {
            Account johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(BigDecimal.ZERO, johnSavingsAccount.getBalance());
            BigDecimal accountBalance = accountService.deposit(new BigDecimal(50000), 1000110001);
            johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(accountBalance, johnSavingsAccount.getBalance());
            BigDecimal newAccountBalance = accountService.withdraw(new BigDecimal(5000), 1000110001);
            assertEquals(newAccountBalance, johnSavingsAccount.getBalance());
            assertNotNull(johnSavingsAccount.getBalance());

        } catch (MavenBankTransactionException e) {
            e.printStackTrace();
        } catch (MavenBankException e) {
            e.printStackTrace();
        } catch (MavenBankInsufficientBankException e) {
            e.printStackTrace();
        }
    }
    @Test
    void withdrawNegative(){
        assertThrows(MavenBankTransactionException.class, () -> accountService.withdraw(new BigDecimal(-50000), 1000110001));
    }
//    @Test
//    void withdrawInsufficientFunds() throws MavenBankException{
//        try{
//            Account johnSavingsAccount = accountService.findAccount(1000110001);
//            BigDecimal accountBalance = accountService.deposit(new BigDecimal(50000), 1000110001);
//            assertEquals(accountBalance, johnSavingsAccount.getBalance());
//        } catch (MavenBankException e) {
//            e.printStackTrace();
//        }
//        assertThrows(MavenBankInsufficientBankException.class, ()->accountService.withdraw(new BigDecimal(70000), 1000110001));
//    }
    @Test
    void withdrawFromAnInvalidAccount() throws MavenBankException {
        assertThrows (MavenBankException.class,
                () -> accountService.withdraw(new BigDecimal (5000), 10110001));
    }
    @Test
    void applyForLoan(){
        LoanRequest johnLoanRequest = new LoanRequest();
        johnLoanRequest.setLoanAmount(BigDecimal.valueOf(50000000));
        johnLoanRequest.setApplyDate(LocalDateTime.now());
        johnLoanRequest.setInterestRate(0.1);
        johnLoanRequest.setStatus(LoanRequestStatus.NEW);
        johnLoanRequest.setTenor(24);
        johnLoanRequest.setTypeOfLoan(LoanType.SME);

        try{
            Account johnCurrentsAccount = accountService.findAccount(1000110002);
            assertNull (johnCurrentsAccount.getAccountLoanRequest());
            johnCurrentsAccount.setAccountLoanRequest(johnLoanRequest);
            assertNotNull (johnCurrentsAccount.getAccountLoanRequest());
            LoanRequestStatus decision = accountService.applyForLoan(johnCurrentsAccount);
            assertNull (decision);
        } catch (MavenBankException e) {
            e.printStackTrace ( );
        }
    }
}





