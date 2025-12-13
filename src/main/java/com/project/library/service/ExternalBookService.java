package com.project.library.service;

import com.project.library.dto.book.BookResponseDto;

public interface ExternalBookService {

    BookResponseDto fetchFromGoogle(String isbn);
    void fetchAndSaveBooks(String query);
}
