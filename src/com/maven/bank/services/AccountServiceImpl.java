package com.maven.bank.services;

import com.maven.bank.dataStore.LoanRequestStatus;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.CurrentAccount;
import com.maven.bank.entities.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.dataStore.CustomerRepo;
import com.maven.bank.dataStore.TransactionType;
import com.maven.bank.entities.SavingsAccount;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientBankException;
import com.maven.bank.exceptions.MavenBankTransactionException;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountServices {


    @Override
    public long openAccount (Customer theCustomer, AccountType type) throws MavenBankException {
        long accountNumber = BigDecimal.ZERO.longValue();
        if(type == AccountType.SAVINGSACCOUNT){
            openSavingsAccount(theCustomer);
        } else if(type == AccountType.CURRENTACCOUNT) {
            openCurrentAccount(theCustomer);
        }

        return accountNumber;
    }

    @Override
    public long openSavingsAccount (Customer theCustomer) throws MavenBankException {
        if (theCustomer == null ) {
            throw new MavenBankException("customer and account type required to open new account");
        }
        SavingsAccount newAccount = new SavingsAccount();
        if (accountTypeExists(theCustomer, newAccount.getClass().getSimpleName())) {
            throw new MavenBankException("Customer already has the requested account type ");
        }
        newAccount.setAccountNumber(BankService.generateAccountNumber());
//        newAccount.setTypeOfAccount(type);
        theCustomer.getAccounts().add(newAccount);
        CustomerRepo.getCustomers().put(theCustomer.getBvn(), theCustomer);
        return newAccount.getAccountNumber();
    }
    @Override
    public long openCurrentAccount (Customer theCustomer) throws MavenBankException {
        if (theCustomer == null) {
            throw new MavenBankException("customer and account type required to open new account");
        }
        CurrentAccount newAccount = new CurrentAccount();
        if(accountTypeExists(theCustomer, newAccount.getClass().getSimpleName())) {
            throw new MavenBankException("Customer already has the requested account type ");
        }
        newAccount.setAccountNumber(BankService.generateAccountNumber());
//        newAccount.setTypeOfAccount(type);
        theCustomer.getAccounts().add(newAccount);
        CustomerRepo.getCustomers().put(theCustomer.getBvn(), theCustomer);
        return newAccount.getAccountNumber();
    }

    @Override
    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankException {
        BigDecimal newBalance;
        Account depositAccount = findAccount(accountNumber);
        validateTransaction(amount, depositAccount);
        newBalance = depositAccount.getBalance().add(amount);
        depositAccount.setBalance(newBalance);
        return newBalance;
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankException {
        TransactionType typeOfTransaction = TransactionType.WITHDRAWAL;
        Account withdrawAccount = findAccount(accountNumber);
        validateTransaction(amount, withdrawAccount);
        try {
            checkForInsufficientFunds(amount, withdrawAccount);
        } catch(MavenBankInsufficientBankException mavenBankInsufficientBankException) {
            this.applyForOverdraft(withdrawAccount);
            try {
                throw mavenBankInsufficientBankException;
            } catch (MavenBankInsufficientBankException e) {
                e.printStackTrace();
            }
        }
        return debitAccount(amount, accountNumber);
    }

    @Override
    public LoanRequestStatus applyForLoan(Account withdrawAccount) {

        return null;
    }

    @Override
    public void applyForOverdraft(Account withdrawAccount) {
            //TODO
    }

    public void validateTransaction(BigDecimal amount, Account account) throws MavenBankException {
        if(amount.compareTo(BigDecimal.ZERO) < BigDecimal.ZERO.intValue()){
            throw new MavenBankTransactionException("Deposit cannot be negative");
        }
        if(account == null) {
            throw new MavenBankException("Account not found");
        }
    }

    public void checkForInsufficientFunds(BigDecimal amount, Account account) throws MavenBankInsufficientBankException {
        if(amount.compareTo(account.getBalance()) > BigDecimal.ZERO.intValue()) {
            throw new MavenBankInsufficientBankException("Insufficient funds!");
        }
    }

    @Override
    public Account findAccount(long accountNumber) {
       Account foundAccount = null;
       boolean accountFound = false;
       for(Customer customer : CustomerRepo.getCustomers().values()){
               for(Account anAccount:customer.getAccounts()){
                   if(anAccount.getAccountNumber() == accountNumber){
                       foundAccount = anAccount;
                       accountFound = true;
                       break;
                   }
               }
               if (accountFound){
                   break;

               }
           }return foundAccount;

    }

    @Override
    public Account findAccount(Customer customer, long accountNumber) {
        return null;
    }

    private boolean accountTypeExists (Customer aCustomer, String name) {
        boolean accountTypeExists = false;
        for (Account customerAccount : aCustomer.getAccounts()) {
            if (customerAccount.getClass().getSimpleName() == name) { //todo
                accountTypeExists = true;
                break;
            }
        }
        return accountTypeExists;


    }

    public BigDecimal debitAccount(BigDecimal amount, long accountNumber)throws MavenBankException{
            Account theAccount = findAccount(accountNumber);
            BigDecimal newBalance = theAccount.getBalance().subtract(amount);
            theAccount.setBalance(newBalance);
            return newBalance;
    }

}