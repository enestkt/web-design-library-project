package com.project.library.dto.loan;

import lombok.Data;

@Data
public class LoanRequestDto {
    private Long userId;
    private Long bookId;
}
