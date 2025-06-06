package com.example.edutrack.transactions.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status", nullable = false)
    private String status = TransactionStatus.PENDING.name().toLowerCase();

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private Date updatedDate = createdDate;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public Transaction() {
    }

    public Transaction(Double amount, Wallet wallet) {
        this.amount = amount;
        this.wallet = wallet;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status.name().toLowerCase();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", wallet=" + wallet +
                '}';
    }
}
