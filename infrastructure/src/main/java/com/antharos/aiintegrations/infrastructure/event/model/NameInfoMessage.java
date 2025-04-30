package com.antharos.aiintegrations.infrastructure.event.model;

import com.antharos.aiintegrations.domain.NameInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NameInfoMessage extends BaseMessage<NameInfo> {

  public NameInfoMessage(String id, String subject, NameInfo content) {
    super(id, subject, content);
  }
}
