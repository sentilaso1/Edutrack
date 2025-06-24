package com.example.edutrack.transactions.exceptions;

public class FinalizedTransactionException extends RuntimeException {
    public FinalizedTransactionException() {
        super();
    }

    public FinalizedTransactionException(String errorMessage) {
        super(errorMessage);
    }
}
