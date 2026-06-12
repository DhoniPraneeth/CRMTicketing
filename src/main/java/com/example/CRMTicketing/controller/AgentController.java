package com.example.CRMTicketing.controller;

import com.example.CRMTicketing.dto.AgentDTO;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.service.AgentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class AgentController {
    private final AgentService agentService;
    @PostMapping
    public ResponseEntity<AgentDTO> createAgent(
            @Valid @NotNull @RequestBody AgentDTO dto) {
        log.info("Creating agent payload: {}", dto.getAgentName());
        return ResponseEntity.ok(agentService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentDTO> getAgentById(@PathVariable @NotNull Long id) {
        log.info("Fetching agent by id: {}", id);
        return ResponseEntity.ok(agentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AgentDTO>> getAllAgents() {
        log.info("Fetching all agents");
        return ResponseEntity.ok(
                agentService.getAllAgents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentDTO> updateAgent(@PathVariable @NotNull Long id,
                                                @Valid @NotNull @RequestBody AgentDTO dto) {
        log.info("Updating agent id: {}", id);
        return ResponseEntity.ok(agentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAgent(@PathVariable @NotNull Long id) {
        log.info("Deleting agent id: {}", id);
        agentService.delete(id);
        return ResponseEntity.ok("Agent deleted successfully");
    }

    @GetMapping("/{id}/workload")
    public ResponseEntity<Integer> getAgentWorkload(@PathVariable @NotNull Long id) {
        log.info("Fetching workload for agent id: {}", id);
        return ResponseEntity.ok(
                agentService.getAgentWorkload(id)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<List<AgentDTO>> getAvailableAgents() {
        log.info("Fetching available agents");
        return ResponseEntity.ok(agentService.getAvailableAgents());
    }

}