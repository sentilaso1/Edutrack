package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.CommonTransaction;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.VnpayTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

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
        Transaction transaction = new Transaction();
        transaction.setId(UUID.fromString((String) row[0]));
        transaction.setInfo((String) row[1]);
        transaction.setAmount((Double) row[2]);
        transaction.setStatus(Transaction.TransactionStatus.valueOf((String) row[3]));
        transaction.setUpdatedDate((java.util.Date) row[4]);
        return transaction;
    }

    private VnpayTransaction mapToVnpayTransaction(Object[] row) {
        VnpayTransaction vnpayTransaction = new VnpayTransaction();
        vnpayTransaction.setTxnRef(UUID.fromString((String) row[0]));
        vnpayTransaction.setOrderInfo((String) row[1]);
        vnpayTransaction.setAmount((Long) row[2]);
        vnpayTransaction.setTransactionStatus((String) row[3]);
        vnpayTransaction.setPayDate((String) row[4]);
        return vnpayTransaction;
    }

    private String getTransactionSql(boolean isVnpay, boolean filterByUserId, int limit) {
        String sql;

        // TODO: Refactor to use a common method for both transaction types
        if (!isVnpay) {
            if (!filterByUserId) {
                sql = """
                            SELECT
                                id,
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
                                id,
                                info,
                                amount,
                                status,
                                updated_date AS date
                            FROM transactions
                            WHERE updated_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            AND wallet_id = :userId
                            ORDER BY date DESC
                            %s
                        """;
            }
        } else {
            if (!filterByUserId) {
                sql = """
                            SELECT
                                txn_ref AS id,
                                order_info AS info,
                                CAST((amount / 100) AS DOUBLE) AS amount,
                                IFNULL(transaction_status, 'pending') AS status,
                                IFNULL(pay_date, created_date) AS date
                            FROM vnpay_transactions
                            WHERE IFNULL(pay_date, created_date) BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            ORDER BY date DESC
                            %s
                        """;
            } else {
                sql = """
                            SELECT
                                txn_ref AS id,
                                order_info AS info,
                                CAST((amount / 100) AS DOUBLE) AS amount,
                                IFNULL(transaction_status, 'pending') AS status,
                                IFNULL(pay_date, created_date) AS date
                            FROM vnpay_transactions
                            WHERE IFNULL(pay_date, created_date) BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                            AND user_id = :userId
                            ORDER BY date DESC
                            %s
                        """;
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
        String transactionSql = getTransactionSql(false, true, limit / 2);
        String vnpayTransactionSql = getTransactionSql(true, true, limit / 2);

        Query transactionQuery = entityManager.createNativeQuery(transactionSql);
        transactionQuery.setParameter("userId", userId);

        Query vnpayTransactionQuery = entityManager.createNativeQuery(vnpayTransactionSql);
        vnpayTransactionQuery.setParameter("userId", userId);

        return executeQuery(transactionQuery, vnpayTransactionQuery);
    }

    private List<CommonTransaction> getAllTransactions(UUID userId) {
        String transactionSql = getTransactionSql(false, true, 0);
        String vnpayTransactionSql = getTransactionSql(true, true, 0);

        Query transactionQuery = entityManager.createNativeQuery(transactionSql);
        transactionQuery.setParameter("userId", userId);

        Query vnpayTransactionQuery = entityManager.createNativeQuery(vnpayTransactionSql);
        vnpayTransactionQuery.setParameter("userId", userId);

        return executeQuery(transactionQuery, vnpayTransactionQuery);
    }

    private List<CommonTransaction> getAllTransactions() {
        String transactionSql = getTransactionSql(false, false, 0);
        String vnpayTransactionSql = getTransactionSql(true, false, 0);

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
