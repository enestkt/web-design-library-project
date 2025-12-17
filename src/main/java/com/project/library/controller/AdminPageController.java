package com.project.library.controller;

import com.project.library.repository.BookRepository;
import com.project.library.repository.LoanRepository;
import com.project.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // HTML döner
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final LoanRepository loanRepo;

    // Admin Dashboard Sayfası
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Thymeleaf sayfasına veri gönderiyoruz (summary.totalUsers gibi kullanacaksın)
        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalBooks", bookRepo.count());
        model.addAttribute("availableBooks", bookRepo.countByAvailable(true));

        return "dashboard"; // templates/dashboard.html dosyasını açar
    }

    // Kitap Listesi Sayfası (İstersen ekleyebilirsin)
    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", bookRepo.findAll());
        return "books"; // templates/books.html olmalı (eğer yapacaksan)
    }
}