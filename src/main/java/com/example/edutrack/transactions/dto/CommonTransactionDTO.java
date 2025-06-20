package com.example.edutrack.transactions.dto;

import com.example.edutrack.common.model.CustomFormatter;
import com.example.edutrack.transactions.model.CommonTransaction;
import com.example.edutrack.transactions.model.Transaction;

import java.util.Date;
import java.util.UUID;

public record CommonTransactionDTO(UUID id, String info, Double amount, String status,
                                   Date date, Double balance, UUID userId) implements CommonTransaction {
    @Override
    public UUID getTransactionId() {
        return id;
    }

    @Override
    public String getTransactionInfo() {
        return info;
    }

    @Override
    public Double getTransactionAmount() {
        return amount;
    }

    @Override
    public String getTransactionAmountFormatted() {
        return CustomFormatter.formatNumberWithSpaces(getTransactionAmount(), true);
    }

    @Override
    public String getTransactionStatus() {
        if (status == null) {
            return Transaction.TransactionStatus.PENDING.toString();
        }

        if (!status.startsWith("0")) {
            return status;
        }

        if ("00".equals(status)) {
            return Transaction.TransactionStatus.COMPLETED.toString();
        }

        return Transaction.TransactionStatus.FAILED.toString();
    }

    @Override
    public Date getTransactionDate() {
        return date;
    }

    @Override
    public Double getTransactionBalance() {
        return balance;
    }

    @Override
    public String getTransactionBalanceFormatted() {
        return CustomFormatter.formatNumberWithSpaces(getTransactionBalance());
    }

    @Override
    public UUID getUserId() {
        return null;
    }
}
