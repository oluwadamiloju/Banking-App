package com.maven.bank.services;

import com.maven.bank.Account;
import com.maven.bank.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.dataStore.CustomerRepo;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientBankException;
import com.maven.bank.exceptions.MavenBankTransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceImplTest {
    private AccountServices accountService;
    private Customer joy;
    private Customer janet;
    private Account joyAccount;
    private Account JanetAccount;

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

//    @Test
//    void openSavingsAccount() {
//
//        assertTrue(CustomerRepo.getCustomers().isEmpty());
//        assertEquals(0, BankService.getCurrentAccountNumber());
//        assertFalse(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
//        try {
//            long newAccountNumber = accountService.openAccount(joy, AccountType.SAVINGS);
//            assertFalse(CustomerRepo.getCustomers().isEmpty());
//            assertEquals(1, BankService.getCurrentAccountNumber());
//            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
//            assertFalse(joy.getAccounts().isEmpty());
//            System.out.println(joy.getAccounts().get(0));
//            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());
//        } catch (MavenBankException ex) {
//            ex.printStackTrace();
//        }


    @Test
    void openAccountWithNoCustomer() {
        assertThrows(MavenBankException.class, () -> accountService.openAccount(null, AccountType.SAVINGS));
    }

    @Test
    void openAccountWithNoAccountType() {
        assertThrows(MavenBankException.class, () -> accountService.openAccount(joy, null));
    }

    @Test
    void openTheSameTypeOfAccountForTheSameCustomer() {
        Optional<Customer> johnOptional = CustomerRepo.getCustomers().values().stream().findFirst();
        Customer john = (johnOptional.isEmpty()) ? null : johnOptional.get();
        assertEquals(1000110003, BankService.getCurrentAccountNumber());
        assertNotNull(john);
        assertNotNull(john.getAccounts());
        assertFalse(john.getAccounts().isEmpty());
        assertEquals(AccountType.SAVINGS, john.getAccounts().get(0).getTypeOfAccount());

        assertThrows(MavenBankException.class, () -> accountService.openAccount(john, AccountType.SAVINGS));
        assertEquals(1000110003, BankService.getCurrentAccountNumber());
        assertEquals(2, john.getAccounts().size());
    }

    @Test
    void openAccountForCurrentAccount() {
        assertTrue(joy.getAccounts().isEmpty());
        assertEquals(1000110003, BankService.getCurrentAccountNumber());

        try {
            long newAccountNumber = accountService.openAccount(joy, AccountType.CURRENT);
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
            long newAccountNumber = accountService.openAccount(joy, AccountType.SAVINGS);

            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertEquals(1, joy.getAccounts().size());
            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());
            newAccountNumber = accountService.openAccount(joy, AccountType.CURRENT);
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
            long newAccountNumber = accountService.openAccount(joy, AccountType.SAVINGS);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertFalse(joy.getAccounts().isEmpty());
            assertEquals(newAccountNumber, joy.getAccounts().get(0).getAccountNumber());

            newAccountNumber = accountService.openAccount(janet, AccountType.SAVINGS);
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
//        @Test void findAccount(){
//            try {
//
//                long accountNumber = joy.getAccounts().get(0).getAccountNumber();
//                accountNumber = accountService.openAccount(joy,AccountType.CURRENT);
//                accountNumber = accountService.openAccount(janet, AccountType.SAVINGS);
//                assertFalse(CustomerRepo.getCustomers().isEmpty());
//                assertEquals(2, CustomerRepo.getCustomers().size());
//                assertEquals(3, BankService.getCurrentAccountNumber());
//
//                Account johnCurrentAccount = accountService.findAccount(2);
//                assertNotNull(johnCurrentAccount);
//
//            }catch (MavenBankException e){
//                e.printStackTrace();
//            }
//        }

    @Test
    void findAccount() {
        try {
            Account johnCurrentAccount = accountService.findAccount(1000110002);
            assertNotNull(johnCurrentAccount);
            assertEquals(1000110002, johnCurrentAccount.getAccountNumber());
            assertEquals(AccountType.CURRENT, johnCurrentAccount.getTypeOfAccount());
        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }

    @Test
    void openAccountForSavingsAccount() {
        assertTrue(joy.getAccounts().isEmpty());
        assertEquals(1000110003, BankService.getCurrentAccountNumber());
        assertFalse(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
        try {
            long newAccount = accountService.openAccount(joy, AccountType.SAVINGS);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(1000110004, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(joy.getBvn()));
            assertFalse(joy.getAccounts().isEmpty());
            assertEquals(newAccount, joy.getAccounts().get(0).getAccountNumber());

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
    void withdraw() {
        try {
            Account johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(BigDecimal.ZERO, johnSavingsAccount.getBalance());
            BigDecimal accountBalance = accountService.deposit(new BigDecimal(50000), 1000110001);
            johnSavingsAccount = accountService.findAccount(1000110001);
            assertEquals(accountBalance, johnSavingsAccount.getBalance());
            BigDecimal newAccountBalance = accountService.withdraw(new BigDecimal(25000), 1000110001);
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
}}





