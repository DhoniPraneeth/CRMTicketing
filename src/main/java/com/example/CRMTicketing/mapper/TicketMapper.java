package com.example.CRMTicketing.mapper;

import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.dto.request.TicketRequestDTO;
import com.example.CRMTicketing.dto.response.TicketResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public Ticket toEntity(TicketRequestDTO dto) {

        Ticket ticket = new Ticket();

        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());

        return ticket;
    }

    public TicketResponseDTO toResponseDTO(Ticket ticket) {

        TicketResponseDTO dto = new TicketResponseDTO();

        dto.setId(Long.valueOf(ticket.getTicketId()));
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());

        if (ticket.getAgent() != null) {
            dto.setAgentName(ticket.getAgent().getAgentName());
        }

        return dto;
    }
}