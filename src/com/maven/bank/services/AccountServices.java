package com.maven.bank.services;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientBankException;
import com.maven.bank.exceptions.MavenBankTransactionException;

import java.math.BigDecimal;

public interface AccountServices {
     long openAccount(Customer theCustomer, AccountType type) throws MavenBankException;
     BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankException, MavenBankInsufficientBankException;
     BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankException, MavenBankInsufficientBankException;
     Account findAccount(long accountNumber) throws MavenBankException;
     Account findAccount(Customer customer, long accountNumber) throws MavenBankException;




}
