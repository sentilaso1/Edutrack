package com.example.edutrack.transactions.service;

import com.example.edutrack.transactions.dto.CommonTransactionDTO;
import com.example.edutrack.transactions.model.CommonTransaction;
import com.example.edutrack.transactions.model.CommonTransactionProjection;
import com.example.edutrack.transactions.repository.CommonTransactionRepository;
import com.example.edutrack.transactions.service.interfaces.CommonTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommonTransactionServiceImpl implements CommonTransactionService {

    private final CommonTransactionRepository commonTransactionRepository;

    @Autowired
    public CommonTransactionServiceImpl(CommonTransactionRepository commonTransactionRepository) {
        this.commonTransactionRepository = commonTransactionRepository;
    }

    private CommonTransactionDTO mapToCommonTransactionDTO(CommonTransactionProjection projection) {
        return new CommonTransactionDTO(
                UUID.fromString(projection.getId()),
                projection.getInfo(),
                projection.getAmount(),
                projection.getStatus(),
                projection.getDate(),
                projection.getBalance(),
                null
        );
    }

    @Override
    public Page<CommonTransaction> findAllByUser(Pageable pageable, String userId) {
        Page<CommonTransactionProjection> projections = commonTransactionRepository.findAllTransactionByUser(userId, pageable);
        return projections.map(this::mapToCommonTransactionDTO);
    }

    @Override
    public Page<CommonTransaction> findAllByUserContaining(Pageable pageable, String userId, String query) {
        Page<CommonTransactionProjection> projections = commonTransactionRepository.findAllTransactionByUserContaining(userId, query, pageable);
        return projections.map(this::mapToCommonTransactionDTO);
    }

    @Override
    public List<CommonTransactionDTO> findAllRecentTransactionsByUser(String userId) {
        List<CommonTransactionProjection> projections = commonTransactionRepository.findAllRecentTransactionsByUser(userId);
        return projections.stream().map(this::mapToCommonTransactionDTO).toList();
    }
}
