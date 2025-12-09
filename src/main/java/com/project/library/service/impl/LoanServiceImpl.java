package com.project.library.service.impl;

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

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public Loan borrowBook(Long userId, Long bookId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is already borrowed");
        }

        // Kitabı ödünç ver
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

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan record not found"));

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        loan.setStatus(Loan.Status.RETURNED);
        loan.setReturnDate(LocalDate.now());

        return loanRepository.save(loan);
    }
}
