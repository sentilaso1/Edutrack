package com.example.edutrack.transactions.model;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CustomFormatter;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "balance", nullable = false)
    private Double balance = 0.0;

    @Column(name = "on_hold", nullable = false)
    private Double onHold = 0.0;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Wallet() {
    }

    public Wallet(User user) {
        this.user = user;
        this.id = user.getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public String getBalanceFormatted() {
        return CustomFormatter.formatNumberWithSpaces(balance);
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getOnHold() {
        return onHold;
    }

    public void setOnHold(Double onHold) {
        this.onHold = onHold;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", balance=" + balance +
                ", onHold=" + onHold +
                ", user=" + user +
                '}';
    }
}
