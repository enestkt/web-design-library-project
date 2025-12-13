package com.project.library.service.impl;

import com.project.library.dto.book.BookResponseDto;
import com.project.library.entity.Author;
import com.project.library.entity.Book;
import com.project.library.entity.Category;
import com.project.library.repository.AuthorRepository;
import com.project.library.repository.BookRepository;
import com.project.library.repository.CategoryRepository;
import com.project.library.service.ExternalBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExternalBookServiceImpl implements ExternalBookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Value("${google.api.key}")
    private String apiKey;

    @Override
    public BookResponseDto fetchFromGoogle(String isbn) {

        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
                + isbn + "&key=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        List items = (List) response.get("items");
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Book not found in Google Books API");
        }

        Map item = (Map) items.get(0);
        Map volumeInfo = (Map) item.get("volumeInfo");

        String title = (String) volumeInfo.get("title");
        String description = (String) volumeInfo.get("description");

        List<String> authorsList = (List<String>) volumeInfo.get("authors");
        String authorName = (authorsList != null && !authorsList.isEmpty())
                ? authorsList.get(0)
                : "Unknown Author";

        Map imageLinks = (Map) volumeInfo.get("imageLinks");
        String imageUrl = (imageLinks != null)
                ? (String) imageLinks.get("thumbnail")
                : null;

        List<String> categories = (List<String>) volumeInfo.get("categories");
        String categoryName = (categories != null && !categories.isEmpty())
                ? categories.get(0)
                : "General";

        // Yazar Kaydet / Bul
        Author author = authorRepository.findByName(authorName)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setName(authorName);
                    return authorRepository.save(a);
                });

        // Kategori Kaydet / Bul
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(categoryName);
                    return categoryRepository.save(c);
                });

        // Book oluştur
        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);
        book.setImageUrl(imageUrl);
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setCategory(category);
        book.setAvailable(true);

        Book saved = bookRepository.save(book);

        // DTO'ya çevir
        return BookResponseDto.builder()
                .id(saved.getId())
                .isbn(saved.getIsbn())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .imageUrl(saved.getImageUrl())
                .authorName(saved.getAuthor().getName())
                .categoryName(saved.getCategory().getName())
                .available(saved.isAvailable())
                .build();
    }
}
