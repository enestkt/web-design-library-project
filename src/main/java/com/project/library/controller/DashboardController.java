package com.project.library.controller;

import com.project.library.dto.dashboard.DashboardResponseDto;
import com.project.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardResponseDto getSummary() {
        return dashboardService.getSummary();
    }
}
