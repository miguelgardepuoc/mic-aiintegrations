package com.antharos.aiintegrations.infrastructure.event.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String id;
  private String subject;
  private String content;
}
