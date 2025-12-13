package com.project.library.dto.book;

import lombok.Data;

@Data
public class BookRequestDto {
    private String isbn;
    private String title;
    private String description;
    private Long authorId;
    private Long categoryId;
}
