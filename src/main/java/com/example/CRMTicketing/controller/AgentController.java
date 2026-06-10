package com.example.CRMTicketing.controller;

import com.example.CRMTicketing.dto.request.AgentRequestDTO;
import com.example.CRMTicketing.dto.response.AgentResponseDTO;
import com.example.CRMTicketing.service.AgentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
@AllArgsConstructor(onConstructor_ = @__())
public class AgentController {

    
    private final AgentService agentService;

    @PostMapping
    public ResponseEntity<AgentResponseDTO>
    createAgent(
            @Valid
            @RequestBody
            AgentRequestDTO dto) {

        return ResponseEntity.ok(
                agentService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponseDTO>
    getAgentById(
            @PathVariable
            String id) {

        return ResponseEntity.ok(
                agentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AgentResponseDTO>>
    getAllAgents() {

        return ResponseEntity.ok(
                agentService.getAllAgents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentResponseDTO>
    updateAgent(
            @PathVariable String id,
            @Valid
            @RequestBody
            AgentRequestDTO dto) {

        return ResponseEntity.ok(
                agentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteAgent(
            @PathVariable String id) {

        agentService.delete(id);

        return ResponseEntity.ok(
                "Agent deleted successfully");
    }
}