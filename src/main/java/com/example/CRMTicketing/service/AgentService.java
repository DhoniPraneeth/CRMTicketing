package com.example.CRMTicketing.service;

import com.example.CRMTicketing.dao.DaoImpl;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dao.Fetcher;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.exception.ResourceNotFoundException;
import com.example.CRMTicketing.cache.UnifiedCacheService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.CRMTicketing.cache.UnifiedCacheService.CacheType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
@Slf4j
public class AgentService {

    private final DaoImpl<Agent> dao;
    private final DaoImpl<Ticket> ticketDao;
    private final UnifiedCacheService cacheService;
    private final Fetcher<Agent> fetcher;

    public boolean save(Agent obj) {

        validateobj(obj);
        if(!fetcher.contains(obj.getId()+"-AGT")) {
            if (obj.getActiveTicketCount() == null) {
                obj.setActiveTicketCount(0);
            }
            if (obj.getAvailabilityStatus() == null) {
                obj.setAvailabilityStatus(true);
            }
        }else{
            Agent agent=fetcher.getById(obj.getId()+"-CMT");
            obj.setAgentId(agent.getAgentId());
        }
        fetcher.save(obj.getId()+"-CMT",obj);
        dao.saveOrUpdate(obj);
        log.info("Save/Updated: "+obj);
        cacheService.put(
                UnifiedCacheService.agentKey(obj.getAgentId()),
                obj,
                CacheType.REDIS
        );

        cacheService.put(
                UnifiedCacheService.agentKey(obj.getAgentId()),
                obj,
                CacheType.LRU
        );

        return true;
    }

    public Agent getById(Long id) {

        validateId(id, "obj id is required");

        Agent cached = cacheService.get(
                UnifiedCacheService.agentKey(id),
                Agent.class,
                CacheType.LRU
        );

        if (cached != null) {
            log.info(CacheType.LRU + " Cache Hit");
            return cached;
        }

        Agent obj = dao.getById(Agent.class, id);

        if (obj == null) {
            throw new ResourceNotFoundException(
                    "obj not found with id " + id
            );
        }

        log.info(CacheType.LRU + " Cache Miss");

        cacheService.put(
                UnifiedCacheService.agentKey(id),
                obj,
                CacheType.LRU
        );

        cacheService.put(
                UnifiedCacheService.agentKey(id),
                obj,
                CacheType.REDIS
        );

        return obj;
    }

    public List<Agent> getAllAgents() {
        List<Agent> objs = dao.get(Agent.class,100, 0);
        log.debug("Agents: "+objs);
        return objs;
    }

    @PostConstruct
    public void loadDataFromDB(){
        List<Agent> agents=getAllAgents();
        fetcher.saveAll(agents);
    }

    public void delete(Long id) {

        validateId(id, "obj id is required");

        Agent obj =
                dao.getById(Agent.class, id);

        if (obj == null) {
            throw new ResourceNotFoundException(
                    "obj not found with id " + id
            );
        }

        dao.deleteById(id,Agent.class);

        cacheService.evict(
                UnifiedCacheService.agentKey(id),
                CacheType.REDIS
        );

        cacheService.evict(
                UnifiedCacheService.agentKey(id),
                CacheType.LRU
        );

        cacheService.evict(
                "obj:list:all",
                CacheType.LRU
        );
    }

    public void resolveTicket(Long ticketId) {

        validateId(ticketId, "Ticket id is required");

        Ticket ticket =ticketDao.getById(Ticket.class, ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException(
                    "Ticket not found with id " + ticketId
            );
        }

        ticket.setStatus(TicketStatus.RESOLVED);

        Agent obj = null;

        if (ticket.getAgentId() != null) {
            obj = dao.getById(
                    Agent.class, ticket.getAgentId()
            );
        }

        if (obj != null) {
            obj.setActiveTicketCount(
                    Math.max(
                            0,
                            safeCount(obj.getActiveTicketCount()) - 1
                    )
            );

            dao.saveOrUpdate(obj);
        }

        ticket.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        ticketDao.saveOrUpdate(ticketId);
    }

    public Integer getAgentWorkload(Long AgentId) {

        validateId(AgentId, "obj id is required");

        Agent obj =
                dao.getById(Agent.class, AgentId);

        if (obj == null) {
            throw new ResourceNotFoundException(
                    "obj not found with id " + AgentId
            );
        }

        return safeCount(obj.getActiveTicketCount());
    }

    public List<Agent> getAvailableAgents() {

        return dao.get(Agent.class,100,0)
                .stream()
                .filter(obj ->
                        Boolean.TRUE.equals(
                                obj.getAvailabilityStatus()
                        )
                )
                .toList();
    }

    private void validateobj(Agent obj) {

        if (obj == null) {
            throw new BadRequestException(
                    "obj payload is required"
            );
        }

        if (obj.getAgentName() == null
                || obj.getAgentName().isBlank()) {

            throw new BadRequestException(
                    "obj name is required"
            );
        }
    }

    private boolean mapAvailability(String status) {

        if (status == null) {
            return false;
        }

        String normalized =
                status.trim().toLowerCase();

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