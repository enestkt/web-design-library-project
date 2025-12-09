package com.project.library.service;

import com.project.library.entity.Loan;

public interface LoanService {

    Loan borrowBook(Long userId, Long bookId);

    Loan returnBook(Long loanId);
}
