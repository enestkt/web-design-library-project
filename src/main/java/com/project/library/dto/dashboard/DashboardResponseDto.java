package com.project.library.dto.dashboard;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardResponseDto {
    private long totalUsers;
    private long totalBooks;
    private long availableBooks;
    private long borrowedBooks;
    private long totalLoans;

    private long todaysLoans;
    private long todaysReturns;

    private Map<String, Long> booksByCategory;
    private Map<String, Long> booksByAuthor;
}
