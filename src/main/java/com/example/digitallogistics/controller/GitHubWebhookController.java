package com.example.digitallogistics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

@RestController
@RequestMapping("/api/github-webhook")
public class GitHubWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookController.class);

    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "active",
            "message", "GitHub Webhook endpoint is ready"
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String delivery,
            @RequestBody(required = false) Map<String, Object> payload) {
        
        log.info("=== GitHub Webhook Received ===");
        log.info("Event: {}", event);
        log.info("Delivery ID: {}", delivery);
        
        if ("ping".equals(event)) {
            log.info("Ping event received - Webhook configured successfully!");
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Pong! Webhook is active"
            ));
        }
        
        if ("push".equals(event) && payload != null) {
            String ref = (String) payload.get("ref");
            log.info("Push event on branch: {}", ref);
            
            if ("refs/heads/main".equals(ref)) {
                log.info("Push to main branch detected - Starting build...");
                triggerBuild();
            }
        }
        
        log.info("================================");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "event", event != null ? event : "unknown",
            "message", "Webhook processed"
        ));
    }
    
    private void triggerBuild() {
        new Thread(() -> {
            try {
                log.info("Starting Maven build and SonarQube analysis...");
                
                ProcessBuilder pb = new ProcessBuilder(
                    "bash", "-c",
                    "cd /Users/chefmoha/Desktop/digitallogistics/LogisticsFlow-api && " +
                    "export JAVA_HOME=/Users/chefmoha/Library/Java/JavaVirtualMachines/ms-17.0.16/Contents/Home && " +
                    "mvn clean test jacoco:report sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=squ_8f26cc6d656a379c8a19bef589fbc99f778a4326"
                );
                
                pb.redirectErrorStream(true);
                Process process = pb.start();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("BUILD: {}", line);
                }
                
                int exitCode = process.waitFor();
                log.info("Build finished with exit code: {}", exitCode);
                
            } catch (Exception e) {
                log.error("Build failed: {}", e.getMessage(), e);
            }
        }).start();
    }
}
