package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.TicketDao;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dto.TicketDTO;
import com.example.CRMTicketing.dto.TicketResponseDTO;
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
            TicketDTO dto) {

        Ticket ticket =
                ticketMapper.toEntity(dto);

        ticketDao.save(ticket);

        return ticketMapper
                .toResponseDTO(ticket);
    }

    @Override
    public TicketResponseDTO getById(
            Long id) {

        return ticketMapper
                .toResponseDTO(
                        ticketDao.getById(id));
    }

    @Override
    public List<TicketResponseDTO>
    getAllTickets() {

        return ticketDao
                .getAllTickets()
                .stream()
                .map(ticketMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDTO update(
            Long id,
            TicketDTO dto) {

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

    @Override
    public void delete(
            Long id) {

        ticketDao.delete(id);
    }

    @Override
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

    @Override
    public void resolveTicket(
            Long ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.RESOLVED);

        ticketDao.update(ticket);
    }

    @Override
    public void closeTicket(
            Long ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.CLOSED);

        ticketDao.update(ticket);
    }
}