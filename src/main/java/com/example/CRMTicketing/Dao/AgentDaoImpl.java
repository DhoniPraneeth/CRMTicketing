package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AgentDaoImpl implements AgentDao{

    private final SessionFactory sessionFactory;
    public Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    
    public void save(Agent agent) {
        getSession().persist(agent);
    }

    
    public Agent getById(String id) {
        return getSession().get(Agent.class,id);
    }

    
    public List<Agent> getAllAgents() {
        return getSession().createQuery("From Agent ",Agent.class)
                .list();
    }

    
    public void delete(String id) {
        Agent a=getSession().get(Agent.class,id);
        if(a!=null){
            getSession().remove(a);
        }
    }

    
    public void update(Agent existing) {
        getSession().merge(existing);
    }
}
