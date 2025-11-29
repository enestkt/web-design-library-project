package com.project.library.service.impl;

import com.project.library.entity.*;
import com.project.library.repository.*;
import com.project.library.service.LoanService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanServiceImpl(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Loan borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user == null || book == null || !book.isAvailable()) {
            return null;
        }

        // kitabı ödünç ver
        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(Loan.Status.BORROWED);

        return loanRepository.save(loan);
    }

    @Override
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);

        if (loan == null) return null;

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        loan.setStatus(Loan.Status.RETURNED);
        loan.setReturnDate(LocalDate.now());

        return loanRepository.save(loan);
    }
}
