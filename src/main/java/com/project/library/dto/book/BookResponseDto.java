package com.project.library.dto.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponseDto {

    private Long id;
    private String isbn;
    private String title;
    private String description;
    private String imageUrl;
    private String authorName;
    private String categoryName;
    private boolean available;
}
