package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Agent;
import org.springframework.stereotype.Component;

import java.util.List;
public interface AgentDao {

    void save(Agent agent);

    Agent getById(Long id);

    List<Agent> getAllAgents();

    void delete(Long id);
}
