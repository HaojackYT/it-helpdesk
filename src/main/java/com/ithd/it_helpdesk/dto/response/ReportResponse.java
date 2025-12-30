package com.ithd.it_helpdesk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    
    private long totalTickets;
    private Map<String, Long> ticketsByStatus;
    private Map<String, Long> ticketsByCategory;
    private Map<String, Long> ticketsByPriority;
}
