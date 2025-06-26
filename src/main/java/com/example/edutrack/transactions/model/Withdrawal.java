package com.example.edutrack.transactions.model;

import com.example.edutrack.common.model.CustomFormatter;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "withdrawals")
public class Withdrawal {
    public static final int MINIMUM_WITHDRAWAL = 5000;

    public enum Status {
        PENDING,
        REJECTED,
        APPROVED,
        COMPLETED;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "response")
    private String response;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private Date updatedDate = createdDate;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public Withdrawal() {
    }

    public Withdrawal(Long amount, Wallet wallet) {
        this.amount = amount;
        this.wallet = wallet;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getAmountFormatted() {
        return CustomFormatter.formatNumberWithSpaces(amount);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
        return "Withdrawal{" +
                "id=" + id +
                ", amount=" + amount +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", wallet=" + wallet +
                '}';
    }
}
