package com.project.library.repository;

import com.project.library.entity.Book;
import com.project.library.entity.Category;
import com.project.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    long countByAvailable(boolean available);
    long countByCategory(Category category);
    long countByAuthor(Author author);
}
