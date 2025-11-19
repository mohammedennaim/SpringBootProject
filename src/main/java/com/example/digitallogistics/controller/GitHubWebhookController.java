package com.example.digitallogistics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/github-webhook")
public class GitHubWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookController.class);

    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String delivery,
            @RequestBody(required = false) Map<String, Object> payload) {
        
        log.info("=== GitHub Webhook Received ===");
        log.info("Event: {}", event);
        log.info("Delivery ID: {}", delivery);
        
        if (payload != null) {
            log.info("Repository: {}", payload.get("repository"));
            log.info("Sender: {}", payload.get("sender"));
            log.info("Action: {}", payload.get("action"));
            
            if ("push".equals(event)) {
                log.info("Ref: {}", payload.get("ref"));
                log.info("Commits: {}", payload.get("commits"));
            }
        }
        
        log.info("================================");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "event", event != null ? event : "unknown",
            "message", "Webhook received successfully"
        ));
    }
}
