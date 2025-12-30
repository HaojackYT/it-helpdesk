package com.ithd.it_helpdesk.service;

import com.ithd.it_helpdesk.dto.response.ReportResponse;
import com.ithd.it_helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TicketRepository ticketRepository;

    public ReportResponse getTicketReport() {
        long totalTickets = ticketRepository.count();
        
        Map<String, Long> ticketsByStatus = new HashMap<>();
        List<Object[]> statusCounts = ticketRepository.countByStatus();
        for (Object[] row : statusCounts) {
            ticketsByStatus.put(row[0].toString(), (Long) row[1]);
        }

        Map<String, Long> ticketsByCategory = new HashMap<>();
        List<Object[]> categoryCounts = ticketRepository.countByCategory();
        for (Object[] row : categoryCounts) {
            ticketsByCategory.put(row[0].toString(), (Long) row[1]);
        }

        Map<String, Long> ticketsByPriority = new HashMap<>();
        List<Object[]> priorityCounts = ticketRepository.countByPriority();
        for (Object[] row : priorityCounts) {
            ticketsByPriority.put(row[0].toString(), (Long) row[1]);
        }

        return ReportResponse.builder()
                .totalTickets(totalTickets)
                .ticketsByStatus(ticketsByStatus)
                .ticketsByCategory(ticketsByCategory)
                .ticketsByPriority(ticketsByPriority)
                .build();
    }
}
