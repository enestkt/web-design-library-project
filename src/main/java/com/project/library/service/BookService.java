package com.project.library.service;

import com.project.library.dto.book.BookRequestDto;
import com.project.library.dto.book.BookResponseDto;

import java.util.List;

public interface BookService {

    List<BookResponseDto> getAllBooks();

    BookResponseDto getBook(Long id);

    BookResponseDto addBook(BookRequestDto dto);

    BookResponseDto updateBook(Long id, BookRequestDto dto);

    void deleteBook(Long id);
}
