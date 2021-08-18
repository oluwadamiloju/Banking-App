package com.maven.bank.services;

import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.dataStore.CustomerRepo;
import com.maven.bank.dataStore.TransactionType;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientBankException;
import com.maven.bank.exceptions.MavenBankTransactionException;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountServices {


        @Override
        public long openAccount (Customer theCustomer, AccountType type) throws MavenBankException {
        if (theCustomer == null || type == null) {
            throw new MavenBankException("customer and account type required to open new account");
        }
        if (accountTypeExists(theCustomer, type)) {
            throw new MavenBankException("Customer already has the requested account type ");
        }

        Account newAccount = new Account();
        newAccount.setAccountNumber(BankService.generateAccountNumber());
        newAccount.setTypeOfAccount(type);
        theCustomer.getAccounts().add(newAccount);
        CustomerRepo.getCustomers().put(theCustomer.getBvn(), theCustomer);
        return newAccount.getAccountNumber();
    }

    @Override
    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankException, MavenBankInsufficientBankException {
        BigDecimal newBalance = BigDecimal.ZERO;
        Account depositAccount = findAccount(accountNumber);
        validateTransaction(amount, depositAccount);
        newBalance = depositAccount.getBalance().add(amount);
        depositAccount.setBalance(newBalance);
        return newBalance;
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankException, MavenBankInsufficientBankException {
        Account withdrawAccount = findAccount(accountNumber);
        validateTransaction(amount, withdrawAccount);
        try {
            checkForInsufficientFunds(amount, TransactionType.WITHDRAWAL, withdrawAccount);
        } catch(MavenBankInsufficientBankException mavenBankInsufficientBankException) {
            this.applyForOverdraft(withdrawAccount);
        }
        BigDecimal newBalance = debitAccount(amount, accountNumber, TransactionType.WITHDRAWAL);
        return newBalance;
    }

    private void applyForOverdraft(Account withdrawAccount) {
            //TODO
    }

    public void validateTransaction(BigDecimal amount, Account account) throws MavenBankException, MavenBankInsufficientBankException {
        if(amount.compareTo(BigDecimal.ZERO) < BigDecimal.ZERO.intValue()){
            throw new MavenBankTransactionException("Deposit cannot be negative");
        }
        if(account == null) {
            throw new MavenBankException("Account not found");
        }
    }

    public void checkForInsufficientFunds(BigDecimal amount, TransactionType type, Account account) throws MavenBankInsufficientBankException {
        if(type == TransactionType.WITHDRAWAL && amount.compareTo(account.getBalance()) > BigDecimal.ZERO.intValue()) {
            throw new MavenBankInsufficientBankException("Insufficient funds!");
        }
    }

    @Override
    public Account findAccount(long accountNumber) throws MavenBankException {
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
    public Account findAccount(Customer customer, long accountNumber) throws MavenBankException {
        return null;
    }

    private boolean accountTypeExists (Customer aCustomer, AccountType type){
        boolean accountTypeExists = false;
        for (Account customerAccount : aCustomer.getAccounts()) {
            if (customerAccount.getTypeOfAccount() == type) {
                accountTypeExists = true;
                break;
            }
        }
        return accountTypeExists;


    }

    public BigDecimal debitAccount(BigDecimal amount, long accountNumber, TransactionType withdrawal)throws MavenBankException{
            Account theAccount = findAccount(accountNumber);
            BigDecimal newBalance = theAccount.getBalance().subtract(amount);
            theAccount.setBalance(newBalance);
            return newBalance;
    }



}