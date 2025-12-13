package com.project.library.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    // JSON verisini işlemek için gerekli araçlar
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${google.api.key}")
    private String apiKey;

    // --- 1. METOD: Tek Kitap Çek (Manuel ISBN ile) ---
    @Override
    public BookResponseDto fetchFromGoogle(String isbn) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&key=" + apiKey;

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            List items = (List) response.get("items");

            if (items == null || items.isEmpty()) {
                throw new RuntimeException("Book not found for ISBN: " + isbn);
            }

            Map item = (Map) items.get(0);
            Map volumeInfo = (Map) item.get("volumeInfo");

            String title = (String) volumeInfo.getOrDefault("title", "No Title");
            String description = (String) volumeInfo.getOrDefault("description", "");

            // Resim
            Map imageLinks = (Map) volumeInfo.get("imageLinks");
            String imageUrl = (imageLinks != null) ? (String) imageLinks.get("thumbnail") : null;

            // Yazar
            List<String> authors = (List<String>) volumeInfo.get("authors");
            String authorName = (authors != null && !authors.isEmpty()) ? authors.get(0) : "Unknown";

            // Kategori
            List<String> categories = (List<String>) volumeInfo.get("categories");
            String categoryName = (categories != null && !categories.isEmpty()) ? categories.get(0) : "General";

            // Yazar ve Kategori Kaydet
            Author author = authorRepository.findByName(authorName)
                    .orElseGet(() -> authorRepository.save(new Author(authorName)));

            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

            // ISBN Kontrolü (Artık hata vermeyecek)
            if (bookRepository.existsByIsbn(isbn)) {
                throw new RuntimeException("Bu kitap zaten kayıtlı!");
            }

            Book book = new Book();
            book.setTitle(title);
            book.setIsbn(isbn);
            book.setDescription(description);
            book.setImageUrl(imageUrl);
            book.setAvailable(true);
            book.setAuthor(author);
            book.setCategory(category);

            Book saved = bookRepository.save(book);

            return convertToDto(saved);

        } catch (Exception e) {
            throw new RuntimeException("Hata: " + e.getMessage());
        }
    }

    // --- 2. METOD: Toplu Kitap Kaydet (/init için) ---
    @Override
    public void fetchAndSaveBooks(String query) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=10&key=" + apiKey;

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode items = root.path("items");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    try {
                        String uniqueId = item.path("id").asText();

                        // Zaten varsa atla
                        if (bookRepository.existsByIsbn(uniqueId)) continue;

                        JsonNode volumeInfo = item.path("volumeInfo");
                        String title = volumeInfo.path("title").asText();
                        String description = volumeInfo.path("description").asText("");
                        if(description.length() > 250) description = description.substring(0, 250) + "...";

                        String imageUrl = "";
                        if (volumeInfo.has("imageLinks")) {
                            imageUrl = volumeInfo.path("imageLinks").path("thumbnail").asText();
                        }

                        String authorName = volumeInfo.has("authors") ? volumeInfo.path("authors").get(0).asText() : "Unknown";
                        String categoryName = query.substring(0, 1).toUpperCase() + query.substring(1);

                        Author author = authorRepository.findByName(authorName)
                                .orElseGet(() -> authorRepository.save(new Author(authorName)));

                        Category category = categoryRepository.findByName(categoryName)
                                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

                        Book book = new Book();
                        book.setIsbn(uniqueId);
                        book.setTitle(title);
                        book.setDescription(description);
                        book.setImageUrl(imageUrl);
                        book.setAvailable(true);
                        book.setAuthor(author);
                        book.setCategory(category);

                        bookRepository.save(book);
                    } catch (Exception e) {
                        continue; // Bir kitap hatası diğerlerini durdurmasın
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BookResponseDto convertToDto(Book book) {
        return BookResponseDto.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .description(book.getDescription())
                .imageUrl(book.getImageUrl())
                .authorName(book.getAuthor().getName())
                .categoryName(book.getCategory().getName())
                .available(book.isAvailable())
                .build();
    }
}