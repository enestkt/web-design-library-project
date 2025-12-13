package com.project.library.service;

import com.project.library.dto.loan.LoanHistoryDto;
import com.project.library.dto.loan.LoanRequestDto;
import com.project.library.dto.loan.LoanResponseDto;

import java.util.List;

public interface LoanService {

    LoanResponseDto borrowBook(LoanRequestDto dto);

    LoanResponseDto returnBook(Long loanId);

    // Yeni eklenen metod
    List<LoanResponseDto> getAllLoans();

    List<LoanHistoryDto> getLoanHistory(Long userId);
}