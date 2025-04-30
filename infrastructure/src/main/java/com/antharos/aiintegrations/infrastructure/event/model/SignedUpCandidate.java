package com.antharos.aiintegrations.infrastructure.event.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignedUpCandidate {
  private UUID jobOfferId;

  private UUID candidateId;

  private String cvFilename;

  // Not interested in the rest of the fields
}
