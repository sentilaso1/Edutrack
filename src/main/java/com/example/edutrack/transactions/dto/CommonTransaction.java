package com.example.edutrack.transactions.dto;

import com.example.edutrack.common.model.CustomFormatter;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.VnpayResult;

import java.util.Date;

public record CommonTransaction(String id, String info, Double amount, String status,
                                Date date, Double balance) {
    public String getTransactionId() {
        return id;
    }

    public String getTransactionInfo() {
        return info;
    }

    public Double getTransactionAmount() {
        return amount;
    }

    public String getTransactionAmountFormatted() {
        return CustomFormatter.formatNumberWithSpaces(getTransactionAmount(), true);
    }

    public String getTransactionStatus() {
        if (status == null) {
            return Transaction.TransactionStatus.PENDING.toString();
        }

        if (!status.startsWith("0")) {
            return status;
        }

        VnpayResult result = VnpayResult.fromCode(status);
        if (result == VnpayResult.TRANSACTION_SUCCESS || result == VnpayResult.TRANSACTION_REFUND_PROCESSING) {
            return Transaction.TransactionStatus.COMPLETED.toString();
        }

        return Transaction.TransactionStatus.FAILED.toString();
    }

    public Date getTransactionDate() {
        return date;
    }

    public Double getTransactionBalance() {
        return balance;
    }

    public String getTransactionBalanceFormatted() {
        return CustomFormatter.formatNumberWithSpaces(getTransactionBalance());
    }
}
