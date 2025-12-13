package com.project.library.dto.loan;

import lombok.Data;

@Data
public class LoanResponseDto {

    private Long id;

    private Long userId;
    private String userName;

    private Long bookId;
    private String bookTitle;

    private String loanDate;
    private String returnDate;

    private String status; // BORROWED / RETURNED
}
