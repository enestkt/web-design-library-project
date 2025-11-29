package com.project.library.controller;

import com.project.library.entity.Author;
import com.project.library.repository.AuthorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Author getById(@PathVariable Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Author add(@RequestBody Author author) {
        return authorRepository.save(author);
    }

    @PutMapping("/{id}")
    public Author update(@PathVariable Long id, @RequestBody Author updated) {
        updated.setId(id);
        return authorRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        authorRepository.deleteById(id);
    }
}
