package com.ithd.it_helpdesk.service;

import com.ithd.it_helpdesk.dto.request.AssignTicketRequest;
import com.ithd.it_helpdesk.dto.request.CreateTicketRequest;
import com.ithd.it_helpdesk.dto.request.UpdateTicketStatusRequest;
import com.ithd.it_helpdesk.dto.response.TicketResponse;
import com.ithd.it_helpdesk.entity.Ticket;
import com.ithd.it_helpdesk.entity.User;
import com.ithd.it_helpdesk.exception.ResourceNotFoundException;
import com.ithd.it_helpdesk.repository.TicketRepository;
import com.ithd.it_helpdesk.repository.UserRepository;
import com.ithd.it_helpdesk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToTicketResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyTickets() {
        UUID currentUserId = getCurrentUserId();
        
        // Check if current user is IT_SUPPORT
        boolean isITSupport = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_IT_SUPPORT"));
        
        List<Ticket> tickets;
        
        if (isITSupport) {
            // IT Support users: only show tickets assigned to them
            tickets = ticketRepository.findByAssignedToId(currentUserId);
        } else {
            // Employees: only show tickets created by them
            tickets = ticketRepository.findByCreatedById(currentUserId);
        }
        
        return tickets.stream()
                .map(this::mapToTicketResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse getTicketById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return mapToTicketResponse(ticket);
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        UUID currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority() != null ? request.getPriority() : Ticket.Priority.MEDIUM)
                .status(Ticket.Status.OPEN)
                .createdBy(currentUser)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToTicketResponse(savedTicket);
    }

    @Transactional
    public TicketResponse assignTicket(UUID ticketId, AssignTicketRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssigneeId()));

        ticket.setAssignedTo(assignee);
        if (ticket.getStatus() == Ticket.Status.OPEN) {
            ticket.setStatus(Ticket.Status.ASSIGNED);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToTicketResponse(updatedTicket);
    }

    @Transactional
    public TicketResponse updateTicketStatus(UUID ticketId, UpdateTicketStatusRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        ticket.setStatus(request.getStatus());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToTicketResponse(updatedTicket);
    }

    @Transactional
    public void deleteTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(ticket);
    }

    private UUID getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    private TicketResponse mapToTicketResponse(Ticket ticket) {
        TicketResponse.UserSummary createdBy = TicketResponse.UserSummary.builder()
                .id(ticket.getCreatedBy().getId())
                .username(ticket.getCreatedBy().getUsername())
                .fullName(ticket.getCreatedBy().getFullName())
                .department(ticket.getCreatedBy().getDepartment())
                .roles(ticket.getCreatedBy().getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .build();

        TicketResponse.UserSummary assignedTo = null;
        if (ticket.getAssignedTo() != null) {
            assignedTo = TicketResponse.UserSummary.builder()
                    .id(ticket.getAssignedTo().getId())
                    .username(ticket.getAssignedTo().getUsername())
                    .fullName(ticket.getAssignedTo().getFullName())
                    .department(ticket.getAssignedTo().getDepartment())
                    .roles(ticket.getAssignedTo().getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toList()))
                    .build();
        }

        List<TicketResponse.CommentResponse> comments = ticket.getComments() != null 
                ? ticket.getComments().stream()
                    .map(comment -> TicketResponse.CommentResponse.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .user(TicketResponse.UserSummary.builder()
                                    .id(comment.getUser().getId())
                                    .username(comment.getUser().getUsername())
                                    .fullName(comment.getUser().getFullName())
                                    .department(comment.getUser().getDepartment())
                                    .roles(comment.getUser().getRoles().stream()
                                            .map(role -> role.getName().name())
                                            .collect(Collectors.toList()))
                                    .build())
                            .createdAt(comment.getCreatedAt())
                            .build())
                    .collect(Collectors.toList())
                : Collections.emptyList();

        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .comments(comments)
                .build();
    }
}
