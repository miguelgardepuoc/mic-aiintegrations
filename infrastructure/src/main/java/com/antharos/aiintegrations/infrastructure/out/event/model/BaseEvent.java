package com.antharos.aiintegrations.infrastructure.out.event.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class BaseEvent<T> implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String eventId;
  private Instant timestamp;
  private String eventName;
  private String aggregateId;
  private String aggregateType;
  private String causedBy;
  private int version;
  private T payload;
}
