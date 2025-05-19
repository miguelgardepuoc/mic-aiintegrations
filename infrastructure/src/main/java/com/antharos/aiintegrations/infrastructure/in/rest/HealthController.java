package com.antharos.aiintegrations.infrastructure.in.rest;

import jakarta.annotation.security.PermitAll;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @PermitAll
  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    Map<String, String> status = new HashMap<>();
    status.put("status", "ok");
    return ResponseEntity.ok(status);
  }
}
