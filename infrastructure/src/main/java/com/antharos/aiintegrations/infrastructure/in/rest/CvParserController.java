package com.antharos.aiintegrations.infrastructure.in.rest;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.infrastructure.security.ManagementOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@Tag(name = "CV Parser", description = "AI-powered operations to extract data from CVs")
public class CvParserController {

  private final CvParserService cvParserService;

  @ManagementOnly
  @PostMapping("/extract-name")
  @Operation(
      summary = "Extract name from CV",
      description = "Uses AI to extract the candidate's name from a CV file")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully extracted name",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NameInfo.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
    @ApiResponse(responseCode = "500", description = "Internal server error during extraction")
  })
  public NameInfo extractName(
      @RequestParam("id") UUID id, @RequestParam("filename") String filename) throws IOException {
    return this.cvParserService.extractName(id, filename);
  }
}
