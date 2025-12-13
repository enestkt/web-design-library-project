package com.project.library.controller;

import com.project.library.dto.book.BookRequestDto;
import com.project.library.dto.book.BookResponseDto;
import com.project.library.service.BookService;
import com.project.library.service.ExternalBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ExternalBookService externalBookService;

    // Tüm kitapları getir
    @GetMapping
    public java.util.List<BookResponseDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    // ID ile kitap getir
    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    // Manuel kitap ekle
    @PostMapping
    public BookResponseDto addBook(@RequestBody BookRequestDto dto) {
        return bookService.addBook(dto);
    }

    // Güncelle
    @PutMapping("/{id}")
    public BookResponseDto updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDto dto
    ) {
        return bookService.updateBook(id, dto);
    }

    // Sil
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    // ⭐ Google Books API üzerinden TEK kitap ekleme (Manuel ISBN ile)
    @PostMapping("/fetch-google")
    public BookResponseDto fetchFromGoogle(@RequestBody Map<String, String> body) {
        String isbn = body.get("isbn");
        return externalBookService.fetchFromGoogle(isbn);
    }

    // ⭐⭐ YENİ EKLENEN: Veritabanını Otomatik Doldur (Toplu Ekleme)
    // Tarayıcıdan http://localhost:8080/api/books/init adresine gidince çalışır.
    @GetMapping("/init")
    public String initializeBooks() {
        // Eğer Interface'e methodu eklemediysen hata vermesin diye casting yapıyoruz
        if (externalBookService instanceof com.project.library.service.impl.ExternalBookServiceImpl) {
            var service = (com.project.library.service.impl.ExternalBookServiceImpl) externalBookService;

            // Farklı kategorilerden kitaplar çek
            service.fetchAndSaveBooks("history");     // Tarih kitapları
            service.fetchAndSaveBooks("science");     // Bilim kitapları
            service.fetchAndSaveBooks("computers");   // Bilgisayar kitapları
            service.fetchAndSaveBooks("fantasy");     // Fantastik kitaplar

            return "Başarılı! Veritabanına History, Science, Computers ve Fantasy kategorilerinde gerçek kitaplar eklendi.";
        }
        return "Hata: ExternalBookService implementasyonu bulunamadı veya interface uyumsuz.";
    }
}