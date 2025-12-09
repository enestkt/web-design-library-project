package com.project.library.controller;

import com.project.library.entity.Book;
import com.project.library.service.BookService;
import com.project.library.service.ExternalBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ExternalBookService externalBookService;

    // Tüm kitapları getir
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // ID ile kitap getir
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    // Manuel kitap ekle
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    // Güncelle
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    // Sil
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    // Google Books API üzerinden kitap ekleme
    @PostMapping("/fetch-google")
    public Book fetchFromGoogle(@RequestBody Map<String, String> body) {
        String isbn = body.get("isbn");
        return externalBookService.fetchFromGoogle(isbn);
    }
}
