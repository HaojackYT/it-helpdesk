package com.ithd.it_helpdesk.repository;

import com.ithd.it_helpdesk.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    
    List<TicketComment> findByTicketIdOrderByCreatedAtDesc(UUID ticketId);
}
