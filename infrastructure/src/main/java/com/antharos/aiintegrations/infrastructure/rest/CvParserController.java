package com.antharos.aiintegrations.infrastructure.rest;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.domain.NameInfo;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
public class CvParserController {

  private final CvParserService cvParserService;

  @PostMapping("/extract-name")
  public NameInfo extractName(@RequestParam("id") UUID id, @RequestParam("file") MultipartFile file)
      throws IOException {
    return this.cvParserService.extractName(id, file.getBytes());
  }
}
