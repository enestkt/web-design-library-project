package com.project.library.controller;

import com.project.library.entity.Loan;
import com.project.library.service.LoanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Ödünç alma
    @PostMapping("/borrow/{userId}/{bookId}")
    public Loan borrow(@PathVariable Long userId, @PathVariable Long bookId) {
        return loanService.borrowBook(userId, bookId);
    }

    // Geri teslim
    @PostMapping("/return/{loanId}")
    public Loan returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }
}
