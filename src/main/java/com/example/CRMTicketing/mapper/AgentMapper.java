package com.example.CRMTicketing.mapper;

import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.dto.AgentDTO;
import org.springframework.stereotype.Component;

@Component
public class AgentMapper {

    public Agent toEntity(AgentDTO dto) {
        Agent agent = new Agent();
        agent.setAgentName(dto.getAgentName());
        agent.setEmail(dto.getEmail());

        String requestedStatus = dto.getAvaialbleStatus();
        boolean availability = false;
        if (requestedStatus != null) {
            String normalized = requestedStatus.trim().toLowerCase();
            availability = "available".equals(normalized)
                    || "avaialble".equals(normalized);
        }
        agent.setAvailabilityStatus(availability);
        agent.setActiveTicketCount(0);

        return agent;
    }

    public AgentDTO toDTO(Agent agent) {
        AgentDTO dto = new AgentDTO();
        dto.setAgentId(agent.getAgentId());
        dto.setAgentName(agent.getAgentName());
        dto.setEmail(agent.getEmail());
        dto.setAvaialbleStatus((agent.getAvailabilityStatus())?"Avaialble":"Not Avaialable");
        return dto;
    }
}