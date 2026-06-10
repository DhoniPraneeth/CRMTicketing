package com.example.CRMTicketing.mapper;

import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.dto.request.AgentRequestDTO;
import com.example.CRMTicketing.dto.response.AgentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AgentMapper {

    public Agent toEntity(AgentRequestDTO dto) {

        Agent agent = new Agent();
        agent.setAgentName(dto.getName());;
        agent.setEmail(dto.getEmail());
        agent.setAvailabilityStatus(dto.isAvailable());
        return agent;
    }

    public AgentResponseDTO toResponseDTO(Agent agent) {
        AgentResponseDTO dto = new AgentResponseDTO();
        dto.setId(agent.getAgentId());
        dto.setName(agent.getAgentName());
        dto.setEmail(agent.getEmail());
        dto.setAvailable(agent.getAvailabilityStatus());
        return dto;
    }
}