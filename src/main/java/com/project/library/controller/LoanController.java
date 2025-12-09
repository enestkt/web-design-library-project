package com.project.library.controller;

import com.project.library.entity.Loan;
import com.project.library.repository.LoanRepository;
import com.project.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final LoanRepository loanRepository;

    // Kitap ödünç alma
    @PostMapping("/borrow/{userId}/{bookId}")
    public ResponseEntity<?> borrow(@PathVariable Long userId,
                                    @PathVariable Long bookId) {

        try {
            Loan loan = loanService.borrowBook(userId, bookId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Kitap iade etme
    @PostMapping("/return/{loanId}")
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {

        try {
            Loan loan = loanService.returnBook(loanId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Kullanıcının ödünç alma geçmişi
    @GetMapping("/user/{userId}")
    public List<Loan> getUserLoans(@PathVariable Long userId) {
        return loanRepository.findAll()
                .stream()
                .filter(l -> l.getUser().getId().equals(userId))
                .toList();
    }
}
