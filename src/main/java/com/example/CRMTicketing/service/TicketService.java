package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.TicketDao;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dto.*;
import com.example.CRMTicketing.dto.response.TicketResponseDTO;
import com.example.CRMTicketing.dto.request.TicketRequestDTO;
import com.example.CRMTicketing.mapper.TicketMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TicketService{
    private final TicketDao ticketDao;
    private final AgentDao agentDao;
    private final TicketMapper ticketMapper;

    public TicketResponseDTO save(
            TicketRequestDTO dto) {
        Ticket ticket =
                ticketMapper.toEntity(dto);

        ticketDao.save(ticket);

        return ticketMapper
                .toResponseDTO(ticket);
    }
    
    public TicketResponseDTO getById(
            Long id) {

        return ticketMapper
                .toResponseDTO(
                        ticketDao.getById(id));
    }

   
    public List<TicketResponseDTO>
    getAllTickets() {

        return ticketDao
                .getAllTickets()
                .stream()
                .map(ticketMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    
    public TicketResponseDTO update(
            Long id,
            TicketRequestDTO dto) {

        Ticket existing =
                ticketDao.getById(id);

        existing.setTitle(
                dto.getTitle());

        existing.setDescription(
                dto.getDescription());

        existing.setPriority(
                dto.getPriority());

        ticketDao.update(existing);

        return ticketMapper
                .toResponseDTO(existing);
    }

    
    public void delete(
            Long id) {

        ticketDao.delete(id);
    }

    
    public void assignAgent(
            Long ticketId,
            Long agentId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        Agent agent =
                agentDao.getById(agentId);

        ticket.setAgent(agent);

        ticketDao.update(ticket);
    }

    
    public void resolveTicket(
            Long ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.RESOLVED);

        ticketDao.update(ticket);
    }

    
    public void closeTicket(
            Long ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.CLOSED);

        ticketDao.update(ticket);
    }
}