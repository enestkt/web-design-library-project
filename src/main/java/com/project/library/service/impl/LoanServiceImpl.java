package com.project.library.service.impl;

import com.project.library.dto.loan.LoanHistoryDto;
import com.project.library.dto.loan.LoanRequestDto;
import com.project.library.dto.loan.LoanResponseDto;
import com.project.library.entity.Book;
import com.project.library.entity.Loan;
import com.project.library.entity.User;
import com.project.library.repository.BookRepository;
import com.project.library.repository.LoanRepository;
import com.project.library.repository.UserRepository;
import com.project.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;

    @Override
    public LoanResponseDto borrowBook(LoanRequestDto dto) {

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepo.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is already borrowed");
        }

        book.setAvailable(false);
        bookRepo.save(book);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(Loan.Status.BORROWED);

        loan = loanRepo.save(loan);

        return toResponse(loan);
    }

    @Override
    public LoanResponseDto returnBook(Long loanId) {

        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(Loan.Status.RETURNED);
        loanRepo.save(loan);

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepo.save(book);

        return toResponse(loan);
    }

    @Override
    public List<LoanHistoryDto> getLoanHistory(Long userId) {
        return loanRepo.findByUserId(userId)
                .stream()
                .map(this::toHistory)
                .collect(Collectors.toList());
    }

    // --- EKLEDİĞİMİZ YENİ METOD (DÜZELTİLMİŞ HALİ) ---
    @Override
    public List<LoanResponseDto> getAllLoans() {
        return loanRepo.findAll()  // loanRepository değil, loanRepo
                .stream()
                .map(this::toResponse) // mapToLoanResponseDto değil, toResponse
                .collect(Collectors.toList());
    }

    // ------------------- MAPPING -------------------

    private LoanResponseDto toResponse(Loan loan) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUser().getId());
        dto.setBookId(loan.getBook().getId());
        dto.setBookTitle(loan.getBook().getTitle());
        dto.setLoanDate(loan.getLoanDate().toString());
        dto.setReturnDate(loan.getReturnDate() != null ? loan.getReturnDate().toString() : null);
        dto.setStatus(loan.getStatus().toString());
        return dto;
    }

    private LoanHistoryDto toHistory(Loan loan) {
        LoanHistoryDto dto = new LoanHistoryDto();
        dto.setLoanId(loan.getId());
        dto.setBookId(loan.getBook().getId());
        dto.setBookTitle(loan.getBook().getTitle());
        dto.setLoanDate(loan.getLoanDate().toString());
        dto.setReturnDate(loan.getReturnDate() != null ? loan.getReturnDate().toString() : null);
        dto.setStatus(loan.getStatus().toString());
        return dto;
    }
}