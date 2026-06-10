package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.AgentDaoImpl;
import com.example.CRMTicketing.Dao.TicketDaoImpl;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dto.request.AgentRequestDTO;
import com.example.CRMTicketing.dto.response.AgentResponseDTO;
import com.example.CRMTicketing.mapper.AgentMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class AgentService {
    
    private final AgentDaoImpl agentDao;
    private final AgentMapper agentMapper;
    private final TicketDaoImpl ticketDao;
    public AgentResponseDTO save(AgentRequestDTO dto) {
        Agent agent = agentMapper.toEntity(dto);
        agent.setAgentId("AGT" + System.currentTimeMillis());
        agentDao.save(agent);
        return agentMapper.toResponseDTO(agent);
    }

    public AgentResponseDTO getById(String id) {
        Agent agent =
                agentDao.getById(id);
        return agentMapper
                .toResponseDTO(agent);
    }
    public List<AgentResponseDTO> getAllAgents() {
        return agentDao
                .getAllAgents()
                .stream()
                .map(agentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    
    public AgentResponseDTO update(
            String id,
            AgentRequestDTO dto) {

        Agent existing =
                agentDao.getById(id);
        existing.setAgentName(
                dto.getName());
        existing.setEmail(
                dto.getEmail());
        existing.setAvailabilityStatus(dto.isAvailable());
        agentDao.update(existing);
        return agentMapper.toResponseDTO(existing);
    }

    
    public void delete(String id) {
        agentDao.delete(id);
    }

    public void resolveTicket(
            String ticketId) {

        Ticket ticket =
                ticketDao.getById(ticketId);

        ticket.setStatus(
                TicketStatus.RESOLVED);

        Agent agent =
                ticket.getAgent();

        if (agent != null) {

            agent.setActiveTicketCount(
                    agent.getActiveTicketCount()
                            - 1);

            agentDao.update(agent);
        }

        ticket.setUpdatedAt(
                LocalDateTime.now());
        ticketDao.update(ticket);
    }
    public Integer getAgentWorkload(
            String agentId) {

        Agent agent =
                agentDao.getById(agentId);

        return agent
                .getActiveTicketCount();
    }
    public List<AgentResponseDTO> getAvailableAgents() {
        return agentDao
                .getAllAgents()
                .stream()
                .filter(agent ->
                        "True".equalsIgnoreCase(String.valueOf(agent.getAvailabilityStatus())))
                .map(agentMapper::toResponseDTO)
                .toList();
    }


}