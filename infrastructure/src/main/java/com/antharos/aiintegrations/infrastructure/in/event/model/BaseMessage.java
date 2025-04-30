package com.antharos.aiintegrations.infrastructure.in.event.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "_type")
public class BaseMessage<T> implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String id;
  private String subject;
  private T content;
}
