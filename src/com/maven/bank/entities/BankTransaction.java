package com.maven.bank.entities;

import com.maven.bank.dataStore.TransactionType;
import com.maven.bank.services.BankService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankTransaction {
    private long txID;
    private LocalDateTime txDate;
    private TransactionType txType;
    private BigDecimal txAmount;

    public BankTransaction(TransactionType txType, BigDecimal txAmount) {
        this.txID = BankService.generateTransactionID();
        this.txType = txType;
        this.txDate = LocalDateTime.now();
        this.txAmount = txAmount;
    }

    public long getTxID() {
        return txID;
    }

    public void setTxID(long id) {
        this.txID = id;
    }

    public LocalDateTime getTxDate() {
        return txDate;
    }

    public void setTxDate(LocalDateTime txDate) {
        this.txDate = txDate;
    }

    public TransactionType getTxType() {
        return txType;
    }

    public void setTxType(TransactionType txType) {
        this.txType = txType;
    }

    public BigDecimal getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(BigDecimal txAmount) {
        this.txAmount = txAmount;
    }
}
