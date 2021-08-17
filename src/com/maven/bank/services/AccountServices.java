package com.maven.bank.services;
import com.maven.bank.Customer;
import com.maven.bank.dataStore.AccountType;
import com.maven.bank.exceptions.MavenBankException;
public interface AccountServices {
     public long openAccount(Customer theCustomer, AccountType type) throws MavenBankException;




}
