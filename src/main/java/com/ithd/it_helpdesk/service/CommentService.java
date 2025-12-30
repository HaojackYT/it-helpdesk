package com.ithd.it_helpdesk.service;

import com.ithd.it_helpdesk.dto.request.CreateCommentRequest;
import com.ithd.it_helpdesk.dto.response.TicketResponse;
import com.ithd.it_helpdesk.entity.Ticket;
import com.ithd.it_helpdesk.entity.TicketComment;
import com.ithd.it_helpdesk.entity.User;
import com.ithd.it_helpdesk.exception.ResourceNotFoundException;
import com.ithd.it_helpdesk.repository.TicketCommentRepository;
import com.ithd.it_helpdesk.repository.TicketRepository;
import com.ithd.it_helpdesk.repository.UserRepository;
import com.ithd.it_helpdesk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketResponse.CommentResponse addComment(UUID ticketId, CreateCommentRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        UUID currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .user(currentUser)
                .content(request.getContent())
                .build();

        TicketComment savedComment = commentRepository.save(comment);

        return TicketResponse.CommentResponse.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .user(TicketResponse.UserSummary.builder()
                        .id(currentUser.getId())
                        .username(currentUser.getUsername())
                        .fullName(currentUser.getFullName())
                        .build())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    private UUID getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}
