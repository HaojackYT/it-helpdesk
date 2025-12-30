package com.ithd.it_helpdesk.controller;

import com.ithd.it_helpdesk.dto.response.ApiResponse;
import com.ithd.it_helpdesk.dto.response.ReportResponse;
import com.ithd.it_helpdesk.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<ReportResponse>> getTicketReport() {
        ReportResponse report = reportService.getTicketReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
