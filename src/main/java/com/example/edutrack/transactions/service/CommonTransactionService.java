package com.example.edutrack.transactions.service;

import com.example.edutrack.transactions.dto.CommonTransaction;
import com.example.edutrack.transactions.dto.CommonTransactionProjection;
import com.example.edutrack.transactions.repository.CommonTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonTransactionService {

    private final CommonTransactionRepository commonTransactionRepository;

    @Autowired
    public CommonTransactionService(CommonTransactionRepository commonTransactionRepository) {
        this.commonTransactionRepository = commonTransactionRepository;
    }

    private CommonTransaction mapToCommonTransaction(CommonTransactionProjection projection) {
        return new CommonTransaction(
                String.valueOf(projection.getId()),
                projection.getInfo(),
                (projection.getType().equals("refund")) ? (- projection.getAmount()) : projection.getAmount(),
                projection.getStatus(),
                projection.getDate(),
                projection.getBalance()
        );
    }

    public Page<CommonTransaction> findAllByUser(Pageable pageable, String userId) {
        Page<CommonTransactionProjection> projections = commonTransactionRepository.findAllTransactionByUser(userId, pageable);
        return projections.map(this::mapToCommonTransaction);
    }

    public Page<CommonTransaction> findAllByUserContaining(Pageable pageable, String userId, String query) {
        Page<CommonTransactionProjection> projections = commonTransactionRepository.findAllTransactionByUserContaining(userId, query, pageable);
        return projections.map(this::mapToCommonTransaction);
    }

    public List<CommonTransaction> findAllRecentTransactionsByUser(String userId) {
        List<CommonTransactionProjection> projections = commonTransactionRepository.findAllRecentTransactionsByUser(userId);
        return projections.stream().map(this::mapToCommonTransaction).toList();
    }
}
