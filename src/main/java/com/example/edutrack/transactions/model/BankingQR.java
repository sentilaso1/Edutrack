package com.example.edutrack.transactions.model;

import com.example.edutrack.accounts.model.User;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Entity
@Table(name = "banking_qrs")
public class BankingQR {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Lob
    @Column(name = "qr_image", columnDefinition = "LONGBLOB")
    private byte[] qrImage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BankingQR() {
    }

    public BankingQR(MultipartFile qrImage, User user) throws IOException {
        this.qrImage = qrImage.getBytes();
        this.user = user;
        this.id = user.getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getQrImage() {
        return qrImage;
    }

    public void setQrImage(MultipartFile qrImage) throws IOException {
        this.qrImage = qrImage.getBytes();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "BankingQR{" +
                "user=" + user +
                ", id=" + id +
                '}';
    }
}
