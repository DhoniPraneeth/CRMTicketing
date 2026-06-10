package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Agent;
import org.springframework.stereotype.Component;

import java.util.List;
public interface AgentDao {

    void save(Agent agent);

    Agent getById(String id);

    List<Agent> getAllAgents();

    void delete(String id);

    void update(Agent existing);
}
