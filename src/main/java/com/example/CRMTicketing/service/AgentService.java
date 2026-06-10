package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.AgentDao;
import com.example.CRMTicketing.Dao.AgentDaoImpl;
import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.dto.request.AgentRequestDTO;
import com.example.CRMTicketing.dto.response.AgentResponseDTO;
import com.example.CRMTicketing.mapper.AgentMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AgentService {
    
    private final AgentDaoImpl agentDao;
    private final AgentMapper agentMapper;
    public AgentResponseDTO save(
            AgentRequestDTO dto) {
        Agent agent =
                agentMapper.toEntity(dto);
        agent.setAgentId("TKT" + System.currentTimeMillis());
        agentDao.save(agent);
        return agentMapper
                .toResponseDTO(agent);
    }

    public AgentResponseDTO getById(
            String id) {
        Agent agent =
                agentDao.getById(id);
        return agentMapper
                .toResponseDTO(agent);
    }
    public List<AgentResponseDTO>
    getAllAgents() {
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
        return agentMapper
                .toResponseDTO(existing);
    }

    
    public void delete(
            String id) {

        agentDao.delete(id);
    }
}