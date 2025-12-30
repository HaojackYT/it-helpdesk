package com.ithd.it_helpdesk.dto.request;

import com.ithd.it_helpdesk.entity.Ticket;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketStatusRequest {
    
    @NotNull(message = "Status is required")
    private Ticket.Status status;
}
