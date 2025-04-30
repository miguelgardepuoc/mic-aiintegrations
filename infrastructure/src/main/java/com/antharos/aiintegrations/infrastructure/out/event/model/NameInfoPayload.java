package com.antharos.aiintegrations.infrastructure.out.event.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NameInfoPayload {
  private UUID candidateId;
  private String name;
  private String surname;
}
