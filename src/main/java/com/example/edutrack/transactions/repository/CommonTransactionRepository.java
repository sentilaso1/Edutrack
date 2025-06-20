package com.example.edutrack.transactions.repository;


import com.example.edutrack.transactions.model.CommonTransactionProjection;
import com.example.edutrack.transactions.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommonTransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query(value = """
                    SELECT *
                    FROM (
                        SELECT
                            BIN_TO_UUID(id) AS id,
                            info,
                            amount,
                            status,
                            balance,
                            updated_date AS date
                        FROM transactions
                        WHERE wallet_id = UUID_TO_BIN(:userId)
                        
                        UNION ALL
                        
                        SELECT
                            BIN_TO_UUID(txn_ref) AS id,
                            order_info AS info,
                            (amount / 100) AS amount,
                            IFNULL(transaction_status, 'pending') AS status,
                            balance,
                            IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME)) AS date
                        FROM vnpay_transactions
                        WHERE user_id = UUID_TO_BIN(:userId)
                    ) AS combined
            """,
            countQuery = """
                            SELECT COUNT(*) FROM (
                                SELECT 1
                                FROM transactions
                                WHERE wallet_id = UUID_TO_BIN(:userId)

                                UNION ALL

                                SELECT 1
                                FROM vnpay_transactions
                                WHERE user_id = UUID_TO_BIN(:userId)
                            ) AS combined
                    """,
            nativeQuery = true)
    Page<CommonTransactionProjection> findAllTransactionByUser(@Param("userId") String userId, Pageable pageable);

    @Query(
            value = """
                    SELECT *
                    FROM (
                        SELECT
                            BIN_TO_UUID(id) AS id,
                            info,
                            amount,
                            status,
                            balance,
                            updated_date AS date
                        FROM transactions
                        WHERE wallet_id = UUID_TO_BIN(:userId)
                          AND LOWER(info) LIKE LOWER(CONCAT('%', :keyword, '%'))

                        UNION ALL

                        SELECT 
                            BIN_TO_UUID(txn_ref) AS id,
                            order_info AS info,
                            (amount / 100) AS amount,
                            IFNULL(transaction_status, 'pending') AS status,
                            balance,
                            IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME)) AS date
                        FROM vnpay_transactions
                        WHERE user_id = UUID_TO_BIN(:userId)
                          AND LOWER(order_info) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    ) AS combined
                    ORDER BY date DESC
                    """,
        countQuery = """
                SELECT COUNT(*) FROM (
                    SELECT 1
                    FROM transactions
                    WHERE wallet_id = UUID_TO_BIN(:userId)
                      AND LOWER(info) LIKE LOWER(CONCAT('%', :keyword, '%'))

                    UNION ALL

                    SELECT 1
                    FROM vnpay_transactions
                    WHERE user_id = UUID_TO_BIN(:userId)
                      AND LOWER(order_info) LIKE LOWER(CONCAT('%', :keyword, '%'))
                ) AS combined
                """,
        nativeQuery = true
    )
    Page<CommonTransactionProjection> findAllTransactionByUserContaining(@Param("userId") String userId, @Param("keyword") String query, Pageable pageable);

    @Query(
            value = """
                    SELECT *
                    FROM (
                        SELECT 
                            BIN_TO_UUID(id) AS id,
                            info,
                            amount,
                            status,
                            balance,
                            updated_date AS date
                        FROM transactions
                        WHERE updated_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                          AND wallet_id = UUID_TO_BIN(:userId)

                        UNION ALL

                        SELECT 
                            BIN_TO_UUID(txn_ref) AS id,
                            order_info AS info,
                            (amount / 100) AS amount,
                            IFNULL(transaction_status, 'pending') AS status,
                            balance,
                            IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME)) AS date
                        FROM vnpay_transactions
                        WHERE IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME))
                              BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                          AND user_id = UUID_TO_BIN(:userId)
                    ) AS combined
                    ORDER BY date
                    """,
            countQuery = """
        SELECT COUNT(*) FROM (
            SELECT 1
            FROM transactions
            WHERE updated_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
              AND wallet_id = UUID_TO_BIN(:userId)

            UNION ALL

            SELECT 1
            FROM vnpay_transactions
            WHERE IFNULL(CONVERT(pay_date, DATETIME), CONVERT(created_date, DATETIME))
                  BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
              AND user_id = UUID_TO_BIN(:userId)
        ) AS combined
        """,
        nativeQuery = true
    )
    List<CommonTransactionProjection> findAllRecentTransactionsByUser(@Param("userId") String userId);
}
