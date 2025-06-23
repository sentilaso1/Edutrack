package com.example.edutrack.transactions.model;

import com.example.edutrack.common.model.CustomFormatter;
import com.example.edutrack.curriculum.model.Course;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private Date updatedDate = createdDate;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Transaction() {
    }

    public Transaction(Double amount, String info, Wallet wallet) {
        this.amount = amount;
        this.info = info;
        this.wallet = wallet;
        this.balance = wallet.getBalance() - amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", info='" + info + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", course=" + course +
                ", balance=" + balance +
                ", wallet=" + wallet +
                '}';
    }
}
