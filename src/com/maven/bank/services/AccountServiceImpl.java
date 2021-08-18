package com.maven.bank.services;

import com.maven.bank.Account;
import com.maven.bank.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.dataStore.CustomerRepo;
import com.maven.bank.exceptions.MavenBankException;
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
    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankException {
            if(amount.compareTo(BigDecimal.ZERO) < 0){
                throw new MavenBankTransactionException("Deposit cannot be negative");
            }
        BigDecimal newBalance = BigDecimal.ZERO;
        Account depositAccount = findAccount(accountNumber);
        newBalance = depositAccount.getBalance().add(amount);
        depositAccount.setBalance(newBalance);
        return newBalance;
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount, long accountNumber, String pin) throws MavenBankException {
        Account withdrawAccount = findAccount(accountNumber);
        if(amount.compareTo(BigDecimal.ZERO)<= BigDecimal.ONE.intValue()){
            throw new MavenBankTransactionException("Invalid withdrawal amount");
        }
        BigDecimal newBalance = BigDecimal.ZERO;

        if(amount.compareTo(BigDecimal.ZERO) > 0 && Account.getAccountPin().equals(pin)) {
            newBalance = withdrawAccount.getBalance().subtract(amount);
            withdrawAccount.setBalance(newBalance);
        }
        return newBalance;
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



}