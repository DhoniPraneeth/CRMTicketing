package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.AgentDaoImpl;
import com.example.CRMTicketing.Dao.TicketDao;
import com.example.CRMTicketing.Dao.TicketDaoImpl;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dto.response.TicketResponseDTO;
import com.example.CRMTicketing.dto.request.TicketRequestDTO;
import com.example.CRMTicketing.mapper.TicketMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.CRMTicketing.Enums.Priority.*;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TicketService{
    private final TicketDaoImpl ticketDao;
    private final AgentDaoImpl agentDao;
    private final TicketMapper ticketMapper;
    public TicketResponseDTO save(
            TicketRequestDTO dto) {

        // Convert DTO -> Entity
        Ticket ticket =
                ticketMapper.toEntity(dto);

        // Generate Ticket ID
        ticket.setTicketId(
                "TKT-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase()
        );

        // Set timestamps
        ticket.setCreatedAt(
                LocalDateTime.now());

        ticket.setUpdatedAt(
                LocalDateTime.now());

        // Default Status
        ticket.setStatus(
                TicketStatus.OPEN);

        // Calculate SLA Deadline
        LocalDateTime slaDeadline =
                calculateSLADeadline(
                        dto.getPriority());

        ticket.setSla_deadline(
                slaDeadline);

        // Auto assign best agent
        Agent bestAgent =
                assignBestAgent();

        if (bestAgent != null) {

            ticket.setAgent(bestAgent);

            // increase workload
            bestAgent.setActiveTicketCount(
                    bestAgent
                            .getActiveTicketCount()
                            + 1);

            agentDao.update(bestAgent);

            ticket.setStatus(
                    TicketStatus.ASSIGNED);
        }

        // Save ticket
        ticketDao.save(ticket);

        return ticketMapper
                .toResponseDTO(ticket);
    }
    private LocalDateTime calculateSLADeadline(Priority priority) {

        LocalDateTime now =
                LocalDateTime.now();

        return switch (priority) {

            case LOW ->
                    now.plusHours(72);

            case MEDIUM ->
                    now.plusHours(48);

            case HIGH ->
                    now.plusHours(24);
        };
    }
    private Agent assignBestAgent() {

        List<Agent> agents =
                agentDao.getAllAgents();

        if (agents.isEmpty()) {
            return null;
        }

        return agents.stream()
                .min(Comparator.comparing(
                        Agent::getActiveTicketCount))
                .orElse(null);
    }


    public TicketResponseDTO getById(String id) {
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
            String id,
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
            String id) {

        ticketDao.delete(id);
    }


    public void assignAgent(String ticketId, String agentId) {
        Ticket ticket =
                ticketDao.getById(ticketId);

        Agent agent =
                agentDao.getById(agentId);

        // remove old agent load
        if (ticket.getAgent() != null) {

            Agent oldAgent =
                    ticket.getAgent();

            oldAgent.setActiveTicketCount(
                    oldAgent
                            .getActiveTicketCount()
                            - 1);

            agentDao.update(oldAgent);
        }

        // assign new agent
        ticket.setAgent(agent);

        // increase workload
        agent.setActiveTicketCount(
                agent.getActiveTicketCount()
                        + 1);

        ticket.setStatus(
                TicketStatus.ASSIGNED);

        agentDao.update(agent);

        ticketDao.update(ticket);
    }


    public void resolveTicket(
            String ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.RESOLVED);

        ticketDao.update(ticket);
    }

    
    public void closeTicket(
            String ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.CLOSED);

        ticketDao.update(ticket);
    }
}