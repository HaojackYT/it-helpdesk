package com.ithd.it_helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ithd.it_helpdesk.security.CustomUserDetails;

@Controller
public class PageController {

    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin/tickets")
    public String adminTickets() {
        return "admin/admin-tickets";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/admin-user";
    }
    
    @GetMapping("/support/dashboard")
    public String supportDashboard(Model model) {
        String displayName = "User";
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                    ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                    : null;

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails user = (CustomUserDetails) principal;
                displayName = user.getFullName() != null && !user.getFullName().isBlank()
                        ? user.getFullName()
                        : user.getUsername();
            }
        } catch (Exception ignored) {
        }

        model.addAttribute("currentUserFullName", displayName);
        return "it-support/itsupport-dashboard";
    }

     @GetMapping({"/support/tickets", "/support/my-tickets"})
    public String supportTickets() {
        return "it-support/itsupport-tickets";
    }
    
    @GetMapping("/employee/dashboard")
    public String employeeDashboard() {
        return "employee/employee-dashboard";
    }

    @GetMapping("/employee/new-ticket")
    public String employeeNewTicket() {
        return "employee/employee-newticket";
    }

    @GetMapping("/employee/my-tickets")
    public String employeeMyTickets() {
        return "employee/employee-myticket";
    }
}
