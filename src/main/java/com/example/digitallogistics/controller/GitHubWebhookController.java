package com.example.digitallogistics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/github-webhook")
public class GitHubWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookController.class);

    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestBody(required = false) Map<String, Object> payload) {
        
        log.info("GitHub Webhook received - Event: {}", event);
        
        if (payload != null) {
            log.info("Payload keys: {}", payload.keySet());
        }
        
        return ResponseEntity.ok(Map.of(
            "status", "received",
            "event", event != null ? event : "unknown"
        ));
    }
}
