package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.TicketDaoImpl;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dto.AgentDTO;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.exception.ResourceNotFoundException;
import com.example.CRMTicketing.mapper.AgentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.CRMTicketing.cache.UnifiedCacheService;
import static com.example.CRMTicketing.cache.UnifiedCacheService.CacheType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class AgentService {

    @Qualifier("AgentRepo")
    private final AgentDao agentDao;
    private final AgentMapper agentMapper;
    private final TicketDaoImpl ticketDao;
    private final UnifiedCacheService cacheService;

    public AgentDTO save(AgentDTO dto) {
        validateAgentDto(dto);
        Agent agent = agentMapper.toEntity(dto);
        if (agent.getActiveTicketCount() == null) {
            agent.setActiveTicketCount(0);
        }
        if (agent.getAvailabilityStatus() == null) {
            agent.setAvailabilityStatus(true);
        }
        agentDao.save(agent);
        // persist to Redis for write-through guarantees and update LRU
        cacheService.put(UnifiedCacheService.agentKey(agent.getAgentId()), agentMapper.toDTO(agent), CacheType.REDIS);
        cacheService.put(UnifiedCacheService.agentKey(agent.getAgentId()), agentMapper.toDTO(agent), CacheType.LRU);
        return agentMapper.toDTO(agent);
    }

    public AgentDTO getById(Long id) {
        validateId(id, "Agent id is required");
        // Try LRU first
        AgentDTO cached = cacheService.get(UnifiedCacheService.agentKey(id), AgentDTO.class, CacheType.LRU);
        if (cached != null) return cached;

        Agent agent = agentDao.getById(id);
        if (agent == null) {
            throw new ResourceNotFoundException("Agent not found with id " + id);
        }

        AgentDTO dto = agentMapper.toDTO(agent);
        cacheService.put(UnifiedCacheService.agentKey(id), dto, CacheType.LRU);
        cacheService.put(UnifiedCacheService.agentKey(id), dto, CacheType.REDIS);
        return dto;
    }

    public List<AgentDTO> getAllAgents() {
        Object cached = cacheService.get("Agent:list:all", Object.class, CacheType.LRU);
        if (cached instanceof java.util.List) {
            try {
                @SuppressWarnings("unchecked")
                java.util.List<AgentDTO> list = (java.util.List<AgentDTO>) cached;
                return list;
            } catch (ClassCastException ignored) {
            }
        }

        java.util.List<AgentDTO> dtos = agentDao.getAllAgents().stream()
                .map(agentMapper::toDTO)
                .collect(Collectors.toList());
        cacheService.put("Agent:list:all", dtos, CacheType.LRU);
        return dtos;
    }

    public AgentDTO update(Long id, AgentDTO dto) {
        validateId(id, "Agent id is required");
        validateAgentDto(dto);

        Agent existing = agentDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Agent not found with id " + id);
        }

        existing.setAgentName(dto.getAgentName());
        existing.setEmail(dto.getEmail());
        existing.setAvailabilityStatus(mapAvailability(dto.getAvaialbleStatus()));
        if (existing.getActiveTicketCount() == null) {
            existing.setActiveTicketCount(0);
        }

        agentDao.update(existing);
        return agentMapper.toDTO(existing);
    }

    public void delete(Long id) {
        validateId(id, "Agent id is required");
        Agent agent = agentDao.getById(id);
        if (agent == null) {
            throw new ResourceNotFoundException("Agent not found with id " + id);
        }
        agentDao.delete(id);
        cacheService.evict(UnifiedCacheService.agentKey(id), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.agentKey(id), CacheType.LRU);
        cacheService.evict("Agent:list:all", CacheType.LRU);
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
    }

    public Integer getAgentWorkload(Long agentId) {
        validateId(agentId, "Agent id is required");
        Agent agent = agentDao.getById(agentId);
        if (agent == null) {
            throw new ResourceNotFoundException("Agent not found with id " + agentId);
        }
        return safeCount(agent.getActiveTicketCount());
    }

    public List<AgentDTO> getAvailableAgents() {
        return agentDao.getAllAgents().stream()
                .filter(agent -> Boolean.TRUE.equals(agent.getAvailabilityStatus()))
                .map(agentMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void validateAgentDto(AgentDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Agent payload is required");
        }
        if (dto.getAgentName() == null || dto.getAgentName().isBlank()) {
            throw new BadRequestException("Agent name is required");
        }
    }

    private boolean mapAvailability(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        return "available".equals(normalized)
                || "avaialble".equals(normalized)
                || "true".equals(normalized);
    }

    private int safeCount(Integer count) {
        return count == null ? 0 : count;
    }

    private void validateId(Long id, String message) {
        if (id == null) {
            throw new BadRequestException(message);
        }
    }
}
