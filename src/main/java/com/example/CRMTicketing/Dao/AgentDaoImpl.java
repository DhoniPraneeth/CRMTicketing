package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.exception.DatabaseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository("AgentRepo")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgentDaoImpl implements AgentDao{

    private final SessionFactory sessionFactory;
    public Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    
    public void save(Agent agent) {
        if (agent.getAvailabilityStatus() == null) {
                agent.setAvailabilityStatus(true);
        }
        if (agent.getActiveTicketCount() == null) {
            agent.setActiveTicketCount(0);
        }
        try {
            getSession().persist(agent);
        } catch (HibernateException ex) {
            log.error("Failed to save agent", ex);
            throw new DatabaseException("Unable to save agent", ex);
        }
    }

    
    public Agent getById(Long id) {
        try {
            return getSession().get(Agent.class,id);
        } catch (HibernateException ex) {
            log.error("Failed to load agent id={}", id, ex);
            throw new DatabaseException("Unable to retrieve agent", ex);
        }
    }

    
    public List<Agent> getAllAgents() {
        try {
            return getSession().createQuery("From Agent ",Agent.class)
                    .list();
        } catch (HibernateException ex) {
            log.error("Failed to fetch agents", ex);
            throw new DatabaseException("Unable to fetch agents", ex);
        }
    }

    
    public void delete(Long id) {
        try {
            Agent a = getSession().get(Agent.class,id);
            if(a != null){
                getSession().remove(a);
            }
        } catch (HibernateException ex) {
            log.error("Failed to delete agent id={}", id, ex);
            throw new DatabaseException("Unable to delete agent", ex);
        }
    }

    
    public void update(Agent existing) {
        try {
            getSession().merge(existing);
        } catch (HibernateException ex) {
            log.error("Failed to update agent id={}", existing.getAgentId(), ex);
            throw new DatabaseException("Unable to update agent", ex);
        }
    }
}
