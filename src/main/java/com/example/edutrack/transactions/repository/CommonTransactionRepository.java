package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.CommonTransaction;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.VnpayTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository
public class CommonTransactionRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final Comparator<CommonTransaction> descendingDateComparator = (t1, t2) -> {
        if (t1.getTransactionDate() == null && t2.getTransactionDate() == null) {
            return 0;
        }
        return t2.getTransactionDate().compareTo(t1.getTransactionDate());
    };

    private Transaction mapToTransaction(Object[] row) {
        System.out.println(Arrays.toString(row));
        Transaction transaction = new Transaction();

        transaction.setId(UUID.fromString(String.valueOf(row[0])));
        transaction.setInfo(String.valueOf(row[1]));
        transaction.setAmount(((Number) row[2]).doubleValue());
        transaction.setStatus(Transaction.TransactionStatus.valueOf(String.valueOf(row[3])));
        transaction.setUpdatedDate((java.util.Date) row[4]);

        return transaction;
    }

    private VnpayTransaction mapToVnpayTransaction(Object[] row) {
        System.out.println(Arrays.toString(row));
        VnpayTransaction vnpayTransaction = new VnpayTransaction();

        vnpayTransaction.setTxnRef(UUID.fromString(String.valueOf(row[0])));
        vnpayTransaction.setOrderInfo(String.valueOf(row[1]));
        vnpayTransaction.setAmount(((Number) row[2]).longValue());
        vnpayTransaction.setTransactionStatus((String) row[3]);
        vnpayTransaction.setPayDate((String) row[4]);

        return vnpayTransaction;
    }

    private String getTransactionSql(boolean isVnpay, UUID userId, int limit) {
        String sql;

        // TODO: Refactor to use a common method for both transaction types
        if (!isVnpay) {
            if (userId == null) {
                sql = """
                            SELECT
                                BIN_TO_UUID(id) AS id,
                                info,
                                amount,
                                status,
                                updated_date AS date
                            FROM transactions
                            WHERE updated_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            ORDER BY date DESC
                            %s
                        """;
            } else {
                sql = """
                            SELECT
                                BIN_TO_UUID(id) AS id,
                                info,
                                amount,
                                status,
                                updated_date AS date
                            FROM transactions
                            WHERE updated_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            AND wallet_id = UUID_TO_BIN(:userId)
                            ORDER BY date DESC
                            %s
                        """;
                sql = sql.replace(":userId", "'" + userId + "'");
            }
        } else {
            if (userId == null) {
                sql = """
                            SELECT
                                BIN_TO_UUID(txn_ref) AS id,
                                order_info AS info,
                                (amount / 100) AS amount,
                                IFNULL(transaction_status, 'pending') AS status,
                                IFNULL(pay_date, created_date) AS date
                            FROM vnpay_transactions
                            WHERE IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME)) BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            ORDER BY date DESC
                            %s
                        """;
            } else {
                sql = """
                            SELECT
                                BIN_TO_UUID(txn_ref) AS id,
                                order_info AS info,
                                (amount / 100) AS amount,
                                IFNULL(transaction_status, 'pending') AS status,
                                IFNULL(pay_date, created_date) AS date
                            FROM vnpay_transactions
                            WHERE IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME)) BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            AND user_id = UUID_TO_BIN(:userId)
                            ORDER BY date DESC
                            %s
                        """;
                sql = sql.replace(":userId", "'" + userId + "'");
            }
        }

        if (limit <= 0) {
            sql = String.format(sql, "");
        } else {
            sql = String.format(sql, "LIMIT " + limit);
        }

        return sql;
    }

    private List<CommonTransaction> executeQuery(Query transactionQuery, Query vnpayTransactionQuery) {
        List<CommonTransaction> transactionList = new ArrayList<>();

        //noinspection unchecked
        List<Object[]> transactionResults = (List<Object[]>) transactionQuery.getResultList();

        //noinspection unchecked
        List<Object[]> vnpayTransactionResults = (List<Object[]>) vnpayTransactionQuery.getResultList();

        for (Object[] row : transactionResults) {
            Transaction transaction = mapToTransaction(row);
            transactionList.add(transaction);
        }

        for (Object[] row : vnpayTransactionResults) {
            VnpayTransaction vnpayTransaction = mapToVnpayTransaction(row);
            transactionList.add(vnpayTransaction);
        }

        transactionList.sort(descendingDateComparator);
        return transactionList;
    }

    private List<CommonTransaction> getAllTransactions(UUID userId, int limit) {
        String transactionSql = getTransactionSql(false, userId, limit / 2);
        String vnpayTransactionSql = getTransactionSql(true, userId, limit / 2);

        Query transactionQuery = entityManager.createNativeQuery(transactionSql);
        Query vnpayTransactionQuery = entityManager.createNativeQuery(vnpayTransactionSql);

        return executeQuery(transactionQuery, vnpayTransactionQuery);
    }

    private List<CommonTransaction> getAllTransactions(UUID userId) {
        String transactionSql = getTransactionSql(false, userId, 0);
        String vnpayTransactionSql = getTransactionSql(true, userId, 0);

        Query transactionQuery = entityManager.createNativeQuery(transactionSql);
        Query vnpayTransactionQuery = entityManager.createNativeQuery(vnpayTransactionSql);

        return executeQuery(transactionQuery, vnpayTransactionQuery);
    }

    private List<CommonTransaction> getAllTransactions() {
        String transactionSql = getTransactionSql(false, null, 0);
        String vnpayTransactionSql = getTransactionSql(true, null, 0);

        Query transactionQuery = entityManager.createNativeQuery(transactionSql);
        Query vnpayTransactionQuery = entityManager.createNativeQuery(vnpayTransactionSql);

        return executeQuery(transactionQuery, vnpayTransactionQuery);
    }

    public List<CommonTransaction> findAll() {
        return getAllTransactions();
    }

    public List<CommonTransaction> findAllByUserId(UUID userId) {
        return getAllTransactions(userId);
    }

    public List<CommonTransaction> findAllByUserIdLimit(UUID userId, int limit) {
        return getAllTransactions(userId, limit);
    }
}
