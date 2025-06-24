package com.example.edutrack.transactions.model;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "vnpay_transactions")
public abstract class VnpayTransaction {
    public static final String TIME_ZONE = "Etc/GMT+7";
    public static final String TIME_FORMAT = "yyyyMMddHHmmss";

    public static final int TRANSACTION_EXPIRATION_TIME = 15;  // minutes
    public static final int FRACTION_SHIFT = 100;
    public static final String ORDER_TYPE_OTHER = "other";

    public static final String COMMAND_PAY = "pay";
    public static final String COMMAND_QUERY = "querydr";
    public static final String COMMAND_REFUND = "refund";

    @Transient
    public final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
    @Transient
    public final SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vnp_version", nullable = false)
    private String version;

    @Column(name = "vnp_command", nullable = false)
    private String command;

    @Transient
    private String tmnCode;

    @Column(name = "vnp_txn_ref")
    private String txnRef;

    @Column(name = "vnp_amount", nullable = false)
    private Long amount;

    @Column(name = "vnp_create_date", nullable = false)
    private String createDate;

    @Column(name = "vnp_ip_addr", nullable = false)
    private String ipAddr;

    @Column(name = "vnp_transaction_no")
    private String transactionNo;

    @Column(name = "vnp_response_code")
    private String responseCode;

    @Column(name = "vnp_transaction_status")
    private String transactionStatus;

    @Column(name = "vnp_secure_hash")
    private String secureHash;

    @Column(name = "vnp_order_info")
    private String orderInfo;

    @Column(name = "is_done")
    private Boolean isDone = Boolean.FALSE;

    @Column(name = "balance")
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public VnpayTransaction() {
    }

    public VnpayTransaction(Long id, String version, String command, String tmnCode, String txnRef, Long amount, String createDate, String ipAddr, String transactionNo, String responseCode, String transactionStatus, String secureHash, String orderInfo, Boolean isDone, Wallet wallet) {
        this.id = id;
        this.version = version;
        this.command = command;
        this.tmnCode = tmnCode;
        this.txnRef = txnRef;
        this.amount = amount;
        this.createDate = createDate;
        this.ipAddr = ipAddr;
        this.transactionNo = transactionNo;
        this.responseCode = responseCode;
        this.transactionStatus = transactionStatus;
        this.secureHash = secureHash;
        this.orderInfo = orderInfo;
        this.isDone = isDone;
        this.wallet = wallet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException();
        }
        this.amount = amount * FRACTION_SHIFT;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getSecureHash() {
        return secureHash;
    }

    public void setSecureHash(String secureHash) {
        this.secureHash = secureHash;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return "VnpayTransaction{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", command='" + command + '\'' +
                ", tmnCode='" + tmnCode + '\'' +
                ", txnRef='" + txnRef + '\'' +
                ", amount=" + amount +
                ", createDate='" + createDate + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", secureHash='" + secureHash + '\'' +
                ", orderInfo='" + orderInfo + '\'' +
                ", isDone=" + isDone +
                ", balance=" + balance +
                ", wallet=" + wallet +
                '}';
    }
}