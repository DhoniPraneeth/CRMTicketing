package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.Entity.HistoryEvent;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.dao.DaoImpl;
import com.example.CRMTicketing.dao.Fetcher;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.exception.ResourceNotFoundException;
import com.example.CRMTicketing.kafka.KafkaConsumerService;
import com.example.CRMTicketing.kafka.KafkaProducerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.CRMTicketing.cache.UnifiedCacheService;
import org.springframework.util.CollectionUtils;

import static com.example.CRMTicketing.cache.UnifiedCacheService.CacheType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class TicketService {
    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;
    private final UnifiedCacheService cacheService;
    private final DaoImpl<Ticket> ticketDao;
    private final DaoImpl<Agent> agentDao;
    private final Fetcher<Ticket> fetch;

    public boolean save(Ticket ticket) {

        validateTicket(ticket);

        if(!fetch.contains(ticket.getId()+"-TKT")){
            ticket.setId(ticket.getId());
            ticket.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            ticket.setStatus(TicketStatus.OPEN);
        }else{
            Ticket ticket1=fetch.getById(ticket.getId()+"-TKT");
            ticket.setTicketId(ticket1.getTicketId());
        }

        ticket.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        ticket.setSla_deadline(calculateSLADeadline(ticket.getPriority()));

        Agent bestAgent = assignBestAgent();
        if (bestAgent != null&&(ticket.getAgentId()==null||ticket.getAgentId()==0)) {
            ticket.setAgentId(bestAgent.getAgentId());
            bestAgent.setActiveTicketCount(Math.max(0, safeCount(bestAgent.getActiveTicketCount()) + 1));
            agentDao.saveOrUpdate(bestAgent);
            ticket.setStatus(TicketStatus.ASSIGNED);
        }

        ticketDao.saveOrUpdate(ticket);
        fetch.save(ticket.getId()+"-TKT",ticket);
        log.info("Map"+fetch.get().stream().toList());

        cacheService.put(UnifiedCacheService.ticketKey(ticket.getTicketId()), ticket, CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
        produceMsg("Ticket",ticket.getTicketId()+"","CREATE");

        return true;
    }

    public Ticket getById(Long fetchId) {
        validateId(fetchId, "Ticket id is required");
        if(!fetch.contains(fetchId+"-TKT"))
            return null;
        Long id=fetch.getById(fetchId+"-TKT").getId();
        Ticket cached = cacheService.get(UnifiedCacheService.ticketKey(id), Ticket.class, CacheType.REDIS);
        if (cached != null) {
            log.info(CacheType.REDIS+"Cache Hit");
            return cached;
        }
        Ticket ticket = ticketDao.getById(Ticket.class, id);
        log.info(CacheType.REDIS+"Cache Mis");
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found with id " + id);
        }
        cacheService.put(UnifiedCacheService.ticketKey(id), ticket, CacheType.REDIS);
        return ticket;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> dtos = ticketDao.get(Ticket.class,100, 0).stream()
                .collect(Collectors.toList());
        return dtos;
    }

    public void delete(Long id) {
        validateId(id, "Ticket id is required");
        Ticket ticket = ticketDao.getById(Ticket.class, id);
        produceMsg("Ticket",String.valueOf(ticket.getTicketId()),"Delete");

        ticketDao.deleteById(id,Ticket.class);
        cacheService.evict(UnifiedCacheService.ticketKey(id), CacheType.REDIS);
        cacheService.evict(UnifiedCacheService.ticketListKey(), CacheType.REDIS);
    }

    private Timestamp calculateSLADeadline(Priority priority) {
        LocalDateTime now = LocalDateTime.now();
        return switch (priority) {
            case LOW -> Timestamp.valueOf(now.plusHours(72));
            case MEDIUM -> Timestamp.valueOf(now.plusHours(48));
            case HIGH -> Timestamp.valueOf(now.plusHours(24));
        };
    }

    private Agent assignBestAgent() {
        List<Agent> agents = agentDao.get(Agent.class,100, 0);
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

    private void validateTicket(Ticket dto) {
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
    @PostConstruct
    public void loadDataFromDB(){
        List<Ticket> tickets=getAllTickets();
        if(!CollectionUtils.isEmpty(tickets)) {
            fetch.saveAll(tickets);
        }
    }
}
