package com.project.library.service;

import com.project.library.entity.Book;
import java.util.List;

public interface BookService {

    List<Book> getAllBooks();

    Book getBook(Long id);

    Book addBook(Book book);

    void deleteBook(Long id);

    Book updateBook(Long id, Book book);
}
