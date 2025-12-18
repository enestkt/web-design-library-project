package com.project.library.dto.loan;

import lombok.Data;

@Data
public class LoanHistoryDto {

    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private String userName;

    private String loanDate;
    private String returnDate;

    private String status;
}
