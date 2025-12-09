package com.project.library.service;

import com.project.library.entity.Author;
import com.project.library.entity.Book;
import com.project.library.entity.Category;
import com.project.library.repository.AuthorRepository;
import com.project.library.repository.BookRepository;
import com.project.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExternalBookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Value("${google.api.key}")
    private String apiKey;

    public Book fetchFromGoogle(String isbn) {

        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
                + isbn + "&key=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        List items = (List) response.get("items");
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Book not found in Google Books API");
        }

        Map item = (Map) items.get(0);
        Map volumeInfo = (Map) item.get("volumeInfo");

        // Temel bilgiler
        String title = (String) volumeInfo.get("title");
        String description = (String) volumeInfo.get("description");

        // Yazar
        List<String> authorsList = (List<String>) volumeInfo.get("authors");
        String authorName = (authorsList != null && !authorsList.isEmpty())
                ? authorsList.get(0)
                : "Unknown";

        // Kapak resmi
        Map imageLinks = (Map) volumeInfo.get("imageLinks");
        String imageUrl = (imageLinks != null)
                ? (String) imageLinks.get("thumbnail")
                : null;

        // Kategori
        List<String> categories = (List<String>) volumeInfo.get("categories");
        String categoryName = (categories != null && !categories.isEmpty())
                ? categories.get(0)
                : "General";

        // Yazar DB'de yoksa oluştur
        Author author = authorRepository.findByName(authorName)
                .orElseGet(() -> authorRepository.save(new Author(authorName)));

        // Kategori DB'de yoksa oluştur
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        // Kitabı oluştur ve kaydet
        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);
        book.setImageUrl(imageUrl);
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setCategory(category);
        book.setAvailable(true);

        return bookRepository.save(book);
    }
}
