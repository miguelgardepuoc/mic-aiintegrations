package com.antharos.aiintegrations.domain.repository;

import com.antharos.aiintegrations.domain.NameInfo;
import java.util.UUID;

public interface MessageProducer {
  void sendNameInfoEvent(NameInfo nameInfo, UUID candidateId);
}
