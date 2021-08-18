package com.maven.bank.services;

import com.maven.bank.Account;
import com.maven.bank.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.dataStore.CustomerRepo;
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

    public BigDecimal transaction(BigDecimal amount, long accountNumber) throws MavenBankException, MavenBankInsufficientBankException {
        BigDecimal newBalance = BigDecimal.ZERO;

        return newBalance;
    }

    @Override
    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankException {
        BigDecimal newBalance = BigDecimal.ZERO;
        Account depositAccount = findAccount(accountNumber);
        validateDeposit(amount, accountNumber);
        newBalance = depositAccount.getBalance().add(amount);
        depositAccount.setBalance(newBalance);
        return newBalance;
    }

    public void validateDeposit(BigDecimal amount, long accountNumber) throws MavenBankException {
        Account depositAcct = findAccount(accountNumber);

        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new MavenBankTransactionException("Deposit cannot be negative");
        }
        if(depositAcct == null) {
            throw new MavenBankException("Account not found");
        }
    }
    @Override
    public BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankException, MavenBankInsufficientBankException {
        Account withdrawAccount = findAccount(accountNumber);
        validateWithdrawal(amount, accountNumber);
        BigDecimal newBalance = debitAccount(amount, accountNumber);

        return newBalance;
    }

    public void validateWithdrawal(BigDecimal amount, long accountNumber) throws MavenBankException, MavenBankInsufficientBankException {
        Account withdrawalAcct = findAccount(accountNumber);
        if(amount.compareTo(BigDecimal.ZERO)< BigDecimal.ONE.intValue()){
            throw new MavenBankTransactionException("Invalid withdrawal amount");
        }
        if(amount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newBalance = withdrawalAcct.getBalance().subtract(amount);
            withdrawalAcct.setBalance(newBalance);
        }

        Account depositAcct = findAccount(accountNumber);
        if(amount.compareTo(depositAcct.getBalance()) > 0) {
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

    public BigDecimal debitAccount(BigDecimal amount, long accountNumber)throws MavenBankException{
            Account theAccount = findAccount(accountNumber);
            BigDecimal newBalance = theAccount.getBalance().subtract(amount);
            theAccount.setBalance(newBalance);
            return newBalance;
    }



}