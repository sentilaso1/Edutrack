package com.example.edutrack.transactions.service.interfaces;

import com.example.edutrack.transactions.dto.CommonTransactionDTO;
import com.example.edutrack.transactions.model.CommonTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommonTransactionService {
    Page<CommonTransaction> findAllByUser(Pageable pageable, String userId);
    Page<CommonTransaction> findAllByUserContaining(Pageable pageable, String userId, String query);
    List<CommonTransactionDTO> findAllRecentTransactionsByUser(String userId);
}
