package com.antharos.aiintegrations.infrastructure.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.domain.NameInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CvParserController.class)
@ContextConfiguration(classes = {CvParserControllerIT.TestConfig.class})
@Import(CvParserController.class)
public class CvParserControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private CvParserService cvParserService;

  private MockMultipartFile validPdfFile;
  private NameInfo expectedNameInfo;

  @SpringBootApplication(scanBasePackages = "com.antharos.aiintegrations")
  static class TestConfig {
    @Bean
    public CvParserService cvParserService() {
      return mock(CvParserService.class);
    }
  }

  @BeforeEach
  void setUp() {
    // Prepare a mock PDF file for testing
    validPdfFile =
        new MockMultipartFile(
            "file", "test-cv.pdf", MediaType.APPLICATION_PDF_VALUE, "Dummy PDF content".getBytes());

    // Prepare expected NameInfo response
    expectedNameInfo = new NameInfo("John", "Doe");
  }

  @Test
  void extractName_ValidPdfFile_ReturnsNameInfo() throws Exception {
    // Arrange
    when(cvParserService.extractName(any())).thenReturn(expectedNameInfo);

    // Act & Assert
    mockMvc
        .perform(
            multipart("/extract-name")
                .file(validPdfFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("John"))
        .andExpect(jsonPath("$.surname").value("Doe"));
  }

  @Test
  void extractName_MissingFile_ReturnsBadRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(multipart("/extract-name").contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }
}
