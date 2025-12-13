package com.project.library.repository;

import com.project.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    long countByLoanDate(LocalDate loanDate);

    long countByReturnDate(LocalDate returnDate);
}
