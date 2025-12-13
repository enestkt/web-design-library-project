package com.project.library.controller;

import com.project.library.dto.loan.LoanHistoryDto;
import com.project.library.dto.loan.LoanRequestDto;
import com.project.library.dto.loan.LoanResponseDto;
import com.project.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/borrow")
    public LoanResponseDto borrow(@RequestBody LoanRequestDto dto) {
        return loanService.borrowBook(dto);
    }

    @PostMapping("/return/{loanId}")
    public LoanResponseDto returnLoan(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }

    @GetMapping("/user/{userId}")
    public List<LoanHistoryDto> history(@PathVariable Long userId) {
        return loanService.getLoanHistory(userId);
    }
}
