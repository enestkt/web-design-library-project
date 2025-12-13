package com.project.library.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private boolean available = true;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
