package com.ithd.it_helpdesk.repository;

import com.ithd.it_helpdesk.entity.Ticket;
import com.ithd.it_helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    
    List<Ticket> findByCreatedBy(User user);
    
    List<Ticket> findByAssignedTo(User user);
    
    List<Ticket> findByStatus(Ticket.Status status);
    
    List<Ticket> findByCategory(Ticket.Category category);
    
    @Query("SELECT t FROM Ticket t WHERE t.createdBy.id = :userId")
    List<Ticket> findByCreatedById(@Param("userId") UUID userId);
    
    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> countByStatus();
    
    @Query("SELECT t.category, COUNT(t) FROM Ticket t GROUP BY t.category")
    List<Object[]> countByCategory();
    
    @Query("SELECT t.priority, COUNT(t) FROM Ticket t GROUP BY t.priority")
    List<Object[]> countByPriority();
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
