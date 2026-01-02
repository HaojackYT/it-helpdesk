package com.ithd.it_helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
    
    @GetMapping("/support/dashboard")
    public String supportDashboard() {
        return "support/support-dashboard";
    }
    
    @GetMapping("/employee/dashboard")
    public String employeeDashboard() {
        return "employee/employee-dashboard";
    }
}
