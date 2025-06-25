package com.example.edutrack.transactions.repository;


import com.example.edutrack.transactions.dto.CommonTransactionProjection;
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
                         info,
                         amount,
                         status,
                         balance,
                         updated_date AS date,
                         'system' AS type
                     FROM transactions
                     WHERE wallet_id = UUID_TO_BIN(:userId)
                       AND balance IS NOT NULL

                     UNION ALL

                     SELECT
                         vnp_order_info AS info,
                         (vnp_amount / 100) AS amount,
                         IFNULL(vnp_transaction_status, 'pending') AS status,
                         balance,
                         CONVERT(vnp_create_date, DATETIME) AS date,
                         vnp_command AS type
                     FROM vnpay_transactions
                     WHERE wallet_id = UUID_TO_BIN(:userId)
                       AND balance IS NOT NULL
                 ) AS combined
            """,
            countQuery = """
                            SELECT COUNT(*) FROM (
                                SELECT 1
                                FROM transactions
                                WHERE wallet_id = UUID_TO_BIN(:userId)
                                  AND balance IS NOT NULL

                                UNION ALL

                                SELECT 1
                                FROM vnpay_transactions
                                WHERE wallet_id = UUID_TO_BIN(:userId)
                                  AND balance IS NOT NULL
                            ) AS combined
                    """,
            nativeQuery = true)
    Page<CommonTransactionProjection> findAllTransactionByUser(@Param("userId") String userId, Pageable pageable);

    @Query(
            value = """
                    SELECT *
                    FROM (
                             SELECT
                                 info,
                                 amount,
                                 status,
                                 balance,
                                 updated_date AS date,
                                 'system' AS type
                             FROM transactions
                             WHERE wallet_id = UUID_TO_BIN(:userId)
                               AND LOWER(info) LIKE LOWER(CONCAT('%', :keyword, '%'))
                               AND balance IS NOT NULL

                             UNION ALL

                             SELECT
                                 vnp_order_info AS info,
                                 (vnp_amount / 100) AS amount,
                                 IFNULL(vnp_transaction_status, 'pending') AS status,
                                 balance,
                                 CONVERT(vnp_create_date, DATETIME) AS date,
                                 vnp_command AS type
                             FROM vnpay_transactions
                             WHERE wallet_id = UUID_TO_BIN(:userId)
                               AND LOWER(vnp_order_info) LIKE LOWER(CONCAT('%', :keyword, '%'))
                               AND balance IS NOT NULL
                         ) AS combined
                    """,
        countQuery = """
                SELECT COUNT(*) FROM (
                    SELECT 1
                    FROM transactions
                    WHERE wallet_id = UUID_TO_BIN(:userId)
                      AND LOWER(info) LIKE LOWER(CONCAT('%', :keyword, '%'))
                      AND balance IS NOT NULL

                    UNION ALL

                    SELECT 1
                    FROM vnpay_transactions
                    WHERE wallet_id = UUID_TO_BIN(:userId)
                      AND LOWER(vnp_order_info) LIKE LOWER(CONCAT('%', :keyword, '%'))
                      AND balance IS NOT NULL
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
                                 info,
                                 amount,
                                 status,
                                 balance,
                                 updated_date AS date,
                                 'system' AS type
                             FROM transactions
                             WHERE wallet_id = UUID_TO_BIN(:userId)
                               AND created_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                               AND balance IS NOT NULL

                             UNION ALL

                             SELECT
                                 vnp_order_info AS info,
                                 (vnp_amount / 100) AS amount,
                                 IFNULL(vnp_transaction_status, 'pending') AS status,
                                 balance,
                                 CONVERT(vnp_create_date, DATETIME) AS date,
                                 vnp_command AS type
                             FROM vnpay_transactions
                             WHERE wallet_id = UUID_TO_BIN(:userId)
                               AND CONVERT(vnp_create_date, DATETIME) BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                               AND balance IS NOT NULL
                         ) AS combined
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT 1
                        FROM transactions
                        WHERE created_date BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                          AND wallet_id = UUID_TO_BIN(:userId)
                          AND balance IS NOT NULL

                        UNION ALL

                        SELECT 1
                        FROM vnpay_transactions
                        WHERE CONVERT(vnp_create_date, DATETIME) BETWEEN NOW() - INTERVAL 30 DAY AND NOW()
                          AND wallet_id = UUID_TO_BIN(:userId)
                          AND balance IS NOT NULL
                    ) AS combined
                    """,
        nativeQuery = true
    )
    List<CommonTransactionProjection> findAllRecentTransactionsByUser(@Param("userId") String userId);
}
