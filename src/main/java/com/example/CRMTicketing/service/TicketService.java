package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.TicketDaoImpl;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dto.TicketDTO;
import com.example.CRMTicketing.dto.*;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.exception.ResourceNotFoundException;
import com.example.CRMTicketing.kafka.KafkaConsumerService;
import com.example.CRMTicketing.kafka.KafkaProducerService;
import com.example.CRMTicketing.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.CRMTicketing.cache.UnifiedCacheService;
import static com.example.CRMTicketing.cache.UnifiedCacheService.CacheType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class TicketService {

    private final TicketDaoImpl ticketDao;
    @Qualifier("AgentRepo")
    private final AgentDao agentDao;
    private final TicketMapper ticketMapper;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;
    private final UnifiedCacheService cacheService;

    public void produceMsg(String name,String ticketId,String action){
        HistoryEvent event=new HistoryEvent();
        log.info("Name"+name+"ticketId"+ticketId);
        event.setObjectType(name);
        event.setObjectId(ticketId);
        event.setAction(action);
        kafkaProducerService.publishHistoryEvent(event);
        log.info(event+"Message");
        kafkaConsumerService.consume(event);
    }

    public TicketDTO save(TicketDTO dto) {
        validateTicketDto(dto);

        Ticket ticket = ticketMapper.toEntity(dto);
        ticket.setCategory(dto.getCategory());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setSla_deadline(calculateSLADeadline(ticket.getPriority()));

        Agent bestAgent = assignBestAgent();
        if (bestAgent != null) {
            ticket.setAgentId(bestAgent.getAgentId());
            bestAgent.setActiveTicketCount(Math.max(0, safeCount(bestAgent.getActiveTicketCount()) + 1));
            agentDao.update(bestAgent);
            ticket.setStatus(TicketStatus.ASSIGNED);
        }

        ticketDao.save(ticket);
        // write-through Redis for ticket entity and evict list cache
        cacheService.put(UnifiedCacheService.ticketKey(ticket.getTicketId()), ticketMapper.toDTO(ticket), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
        produceMsg("Ticket",ticket.getTicketId()+"","CREATE");
        return ticketMapper.toDTO(ticket);
    }

    public TicketDTO getById(Long id) {
        validateId(id, "Ticket id is required");
        // Try Redis cache first
        TicketDTO cached = cacheService.get(UnifiedCacheService.ticketKey(id), TicketDTO.class, CacheType.REDIS);
        if (cached != null) return cached;

        Ticket ticket = ticketDao.getById(id);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found with id " + id);
        }

        TicketDTO dto = ticketMapper.toDTO(ticket);
        cacheService.put(UnifiedCacheService.ticketKey(id), dto, CacheType.REDIS);
        return dto;
    }

    public List<TicketDTO> getAllTickets() {
        Object cached = cacheService.get(UnifiedCacheService.ticketListKey(), Object.class, CacheType.REDIS);
        if (cached instanceof java.util.List) {
            try {
                @SuppressWarnings("unchecked")
                java.util.List<TicketDTO> list = (java.util.List<TicketDTO>) cached;
                return list;
            } catch (ClassCastException ignored) {
            }
        }

        java.util.List<TicketDTO> dtos = ticketDao.getAllTickets().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
        cacheService.put(UnifiedCacheService.ticketListKey(), dtos, CacheType.REDIS);
        return dtos;
    }

    public TicketDTO update(Long id, TicketDTO dto) {
        validateId(id, "Ticket id is required");
        validateTicketDto(dto);

        Ticket existing = ticketDao.getById(id);
        produceMsg("Ticket",String.valueOf(id),"UPDATE");
        if (existing == null) {
            throw new ResourceNotFoundException("Ticket not found with id " + id);
        }

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setPriority(dto.getPriority());
        ticketDao.update(existing);

        // update redis cache and evict list
        cacheService.put(UnifiedCacheService.ticketKey(id), ticketMapper.toDTO(existing), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);

        return ticketMapper.toDTO(existing);
    }

    public void delete(Long id) {
        validateId(id, "Ticket id is required");
        Ticket ticket = ticketDao.getById(id);
        produceMsg("Ticket",String.valueOf(ticket.getTicketId()),"Delete");

        ticketDao.delete(id);
        cacheService.evict(UnifiedCacheService.ticketKey(id), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
    }

    public void assignAgent(Long ticketId, Long agentId) {
        validateId(ticketId, "Ticket id is required");
        validateId(agentId, "Agent id is required");

        Ticket ticket = ticketDao.getById(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found with id " + ticketId);
        }

        Agent agent = agentDao.getById(agentId);
        if (agent == null) {
            throw new ResourceNotFoundException("Agent not found with id " + agentId);
        }

        if (ticket.getAgentId() != null) {
            Agent assignedAgent = agentDao.getById(ticket.getAgentId());
            if (assignedAgent != null) {
                assignedAgent.setActiveTicketCount(Math.max(0, safeCount(assignedAgent.getActiveTicketCount()) - 1));
                agentDao.update(assignedAgent);
            }
        }

        ticket.setAgentId(agent.getAgentId());
        agent.setActiveTicketCount(safeCount(agent.getActiveTicketCount()) + 1);
        ticket.setStatus(TicketStatus.ASSIGNED);

        agentDao.update(agent);
        ticketDao.update(ticket);
        cacheService.put(UnifiedCacheService.ticketKey(ticketId), ticketMapper.toDTO(ticket), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
    }

    public void resolveTicket(Long ticketId) {
        validateId(ticketId, "Ticket id is required");

        Ticket ticket = ticketDao.getById(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found with id " + ticketId);
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        Agent agent = ticket.getAgentId() == null ? null : agentDao.getById(ticket.getAgentId());
        if (agent != null) {
            agent.setActiveTicketCount(Math.max(0, safeCount(agent.getActiveTicketCount()) - 1));
            agentDao.update(agent);
        }
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketDao.update(ticket);
        cacheService.put(UnifiedCacheService.ticketKey(ticketId), ticketMapper.toDTO(ticket), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
    }

    public void closeTicket(Long ticketId) {
        validateId(ticketId, "Ticket id is required");

        Ticket ticket = ticketDao.getById(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found with id " + ticketId);
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticketDao.update(ticket);
        cacheService.put(UnifiedCacheService.ticketKey(ticketId), ticketMapper.toDTO(ticket), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
    }

    private LocalDateTime calculateSLADeadline(Priority priority) {
        LocalDateTime now = LocalDateTime.now();
        return switch (priority) {
            case LOW -> now.plusHours(72);
            case MEDIUM -> now.plusHours(48);
            case HIGH -> now.plusHours(24);
        };
    }

    private Agent assignBestAgent() {
        List<Agent> agents = agentDao.getAllAgents();
        if (agents.isEmpty()) {
            return null;
        }
        return agents.stream()
                .min(Comparator.comparingInt(agent -> safeCount(agent.getActiveTicketCount())))
                .orElse(null);
    }

    private int safeCount(Integer count) {
        return count == null ? 0 : count;
    }

    private void validateTicketDto(TicketDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Ticket payload is required");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Ticket title is required");
        }
        if (dto.getPriority() == null) {
            throw new BadRequestException("Ticket priority is required");
        }
    }

    private void validateId(Long id, String message) {
        if (id == null) {
            throw new BadRequestException(message);
        }
    }
}
