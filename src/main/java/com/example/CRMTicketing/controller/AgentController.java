package com.example.CRMTicketing.controller;

import com.example.CRMTicketing.Entity.Agent;
import com.example.CRMTicketing.dao.Fetcher;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.exception.ResourceNotFoundException;
import com.example.CRMTicketing.service.AgentService;

import io.lettuce.core.json.JsonObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor()
public class AgentController {
    private final AgentService agentService;
    @PostMapping
    public ResponseEntity<Map<String,?>> createOrUpdateAgent(@Valid @RequestBody Agent dto) {
        log.info("Creating agent payload: {}", dto.getAgentName());
        if(agentService.save(dto))
        return new ResponseEntity<>(Map.of("message","Sucessful Transcation"),HttpStatus.OK);
        return new ResponseEntity<>(Map.of("message",new BadRequestException("Unable to do update/save")),HttpStatus.BAD_GATEWAY);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,?>> getAgentById(@PathVariable @NotNull Long id) {
        log.info("Fetching agent by id: {}", id);
        return ResponseEntity.ok(Map.of("Data",agentService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<Map<String,?>> getAllAgents() {
        log.info("Fetching all agents");
        List<Agent> agents=agentService.getAllAgents();
        return new ResponseEntity<>(Map.of("Agents: ",agents),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAgent(@PathVariable @NotNull Long id) {
        log.info("Deleting agent id: {}", id);
        agentService.delete(id);
        return ResponseEntity.ok("Agent deleted successfully");
    }
}