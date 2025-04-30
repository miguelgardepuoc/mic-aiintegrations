package com.antharos.aiintegrations.infrastructure.out.event.model;

import com.antharos.aiintegrations.domain.NameInfo;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NameInfoPayloadMapper {

  default NameInfoPayload toPayload(NameInfo domain, UUID candidateId) {
    NameInfoPayload payload = new NameInfoPayload();
    payload.setCandidateId(candidateId);
    payload.setName(domain.name());
    payload.setSurname(domain.surname());
    return payload;
  }
}
