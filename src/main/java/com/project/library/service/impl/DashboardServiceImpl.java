package com.project.library.service.impl;

import com.project.library.dto.dashboard.DashboardResponseDto;
import com.project.library.repository.*;
import com.project.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    @Override
    public DashboardResponseDto getSummary() {

        DashboardResponseDto dto = new DashboardResponseDto();

        // BASIC COUNTS
        dto.setTotalUsers(userRepository.count());
        dto.setTotalBooks(bookRepository.count());
        dto.setAvailableBooks(bookRepository.countByAvailable(true));
        dto.setBorrowedBooks(bookRepository.countByAvailable(false));
        dto.setTotalLoans(loanRepository.count());

        // TODAY COUNTS
        LocalDate today = LocalDate.now();
        dto.setTodaysLoans(loanRepository.countByLoanDate(today));
        dto.setTodaysReturns(loanRepository.countByReturnDate(today));

        // CATEGORY-WISE BOOK COUNT
        dto.setBooksByCategory(
                categoryRepository.findAll().stream()
                        .collect(Collectors.toMap(
                                c -> c.getName(),
                                c -> bookRepository.countByCategory(c)
                        ))
        );

        // AUTHOR-WISE BOOK COUNT
        dto.setBooksByAuthor(
                authorRepository.findAll().stream()
                        .collect(Collectors.toMap(
                                a -> a.getName(),
                                a -> bookRepository.countByAuthor(a)
                        ))
        );

        return dto;
    }
}
