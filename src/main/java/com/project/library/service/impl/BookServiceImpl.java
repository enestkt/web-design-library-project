package com.project.library.service.impl;

import com.project.library.dto.book.BookRequestDto;
import com.project.library.dto.book.BookResponseDto;
import com.project.library.entity.Author;
import com.project.library.entity.Book;
import com.project.library.entity.Category;
import com.project.library.repository.AuthorRepository;
import com.project.library.repository.BookRepository;
import com.project.library.repository.CategoryRepository;
import com.project.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDto getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return convertToDto(book);
    }

    @Override
    public BookResponseDto addBook(BookRequestDto dto) {

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setAvailable(true);
        book.setAuthor(author);
        book.setCategory(category);

        bookRepository.save(book);

        return convertToDto(book);
    }

    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto dto) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setAuthor(author);
        book.setCategory(category);

        bookRepository.save(book);

        return convertToDto(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
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
