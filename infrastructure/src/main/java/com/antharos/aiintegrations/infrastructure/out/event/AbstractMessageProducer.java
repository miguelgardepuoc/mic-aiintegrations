package com.antharos.aiintegrations.infrastructure.out.event;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import com.antharos.aiintegrations.infrastructure.out.event.model.BaseEvent;
import com.antharos.aiintegrations.infrastructure.out.event.model.NameInfoPayload;
import com.antharos.aiintegrations.infrastructure.out.event.model.NameInfoPayloadMapper;
import java.time.Instant;
import java.util.UUID;

public abstract class AbstractMessageProducer implements MessageProducer {

  private static final String NAME_EXTRACTED_SUBJECT = "COMPLETE_NAME_EXTRACTED_FROM_CV";
  protected final NameInfoPayloadMapper mapper;

  public AbstractMessageProducer(NameInfoPayloadMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void sendNameInfoEvent(NameInfo nameInfo, UUID candidateId) {
    BaseEvent<NameInfoPayload> event =
        new BaseEvent<>(
            UUID.randomUUID().toString(),
            Instant.now(),
            NAME_EXTRACTED_SUBJECT,
            candidateId.toString(),
            null,
            null,
            1,
            this.mapper.toPayload(nameInfo, candidateId));
    this.sendMessage(event);
  }

  public abstract void sendMessage(BaseEvent<NameInfoPayload> event);
}
