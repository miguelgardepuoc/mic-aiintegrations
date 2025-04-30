package com.antharos.aiintegrations.infrastructure.in.event.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;

public class EventMapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static SignedUpCandidate mapToSignedUpCandidateEvent(String jsonMessage)
      throws IOException {
    JsonNode root = objectMapper.readTree(jsonMessage);
    JsonNode payload = root.get("payload");
    SignedUpCandidate event = new SignedUpCandidate();
    event.setCandidateId(UUID.fromString(payload.get("id").asText()));
    event.setCvFilename(payload.get("cvFilename").asText());
    return event;
  }
}
