package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByWallet(Wallet wallet);

    @Query("SELECT t FROM Transaction t WHERE t.wallet.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(t.amount) " +
                    "FROM Mentor m " +
                    "JOIN m.applications cm " +
                    "JOIN Enrollment e ON e.courseMentor = cm " +
                    "JOIN Transaction t ON t = e.transaction " +
                    "WHERE t.status = 'COMPLETED' " +
                    "AND t.createdDate >= :startDate")
    Double getTotalRevenueFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'COMPLETED'")
    Double getTotalRevenue();

    @Query("SELECT DATE(t.createdDate) as date, SUM(t.amount) as revenue " +
                    "FROM Transaction t " +
                    "WHERE t.status = 'COMPLETED' " +
                    "AND t.createdDate >= :startDate " +
                    "AND t.amount > 0 " +
                    "GROUP BY DATE(t.createdDate) " +
                    "ORDER BY DATE(t.createdDate)")
    List<Object[]> getDailyRevenueFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT YEAR(t.createdDate) as year, MONTH(t.createdDate) as month, SUM(t.amount) as revenue " +
                    "FROM Transaction t " +
                    "WHERE t.status = 'COMPLETED' " +
                    "AND t.createdDate >= :startDate " +
                    "AND t.amount > 0 " +
                    "GROUP BY YEAR(t.createdDate), MONTH(t.createdDate) " +
                    "ORDER BY YEAR(t.createdDate), MONTH(t.createdDate)")
    List<Object[]> getMonthlyRevenueFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT YEAR(t.createdDate) as year, QUARTER(t.createdDate) as quarter, SUM(t.amount) as revenue " +
                    "FROM Transaction t " +
                    "WHERE t.status = 'COMPLETED' " +
                    "AND t.amount > 0 " +
                    "AND t.createdDate >= :startDate " +
                    "GROUP BY YEAR(t.createdDate), QUARTER(t.createdDate) " +
                    "ORDER BY YEAR(t.createdDate), QUARTER(t.createdDate)")
    List<Object[]> getQuarterlyRevenueFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT YEAR(t.createdDate) as year, SUM(t.amount) as revenue " +
                    "FROM Transaction t " +
                    "WHERE t.status = 'COMPLETED' " +
                    "AND t.amount > 0 " +
                    "AND t.createdDate >= :startDate " +
                    "GROUP BY YEAR(t.createdDate) " +
                    "ORDER BY YEAR(t.createdDate)")
    List<Object[]> getYearlyRevenueFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT cm.mentor.id, u.fullName, SUM(t.amount) as totalRevenue " +
                    "FROM Transaction t " +
                    "JOIN Enrollment e ON e.transaction = t " +
                    "JOIN CourseMentor cm ON cm = e.courseMentor " +
                    "JOIN User u ON u.id = cm.mentor.id " +
                    "WHERE t.status = 'COMPLETED' " +
                    "AND t.createdDate >= :startDate " +
                    "AND cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED " +
                    "GROUP BY cm.mentor.id, u.fullName " +
                    "ORDER BY totalRevenue DESC")
    List<Object[]> getTopMentorsByRevenue(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT cm.mentor.id) FROM Transaction t " +
            "JOIN CourseMentor cm ON t.course.id = cm.id " +
            "WHERE t.createdDate >= :startDate AND cm.status = 'ACCEPTED'")
    Long getActiveMentorCount(@Param("startDate") LocalDateTime startDate);
}
