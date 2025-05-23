package com.antharos.aiintegrations.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.BlobRepository;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CvParserServiceTest {

  private NameExtractionService nameExtractionService;
  private MessageProducer messageProducer;
  private BlobRepository blobRepository;
  private CvParserService cvParserService;

  @BeforeEach
  void setUp() {
    nameExtractionService = mock(NameExtractionService.class);
    messageProducer = mock(MessageProducer.class);
    blobRepository = mock(BlobRepository.class);
    cvParserService = new CvParserService(nameExtractionService, messageProducer, blobRepository);
  }

  @Test
  void whenExtractNameIsCalled_thenProcessesFileAndSendsEvent() throws IOException {
    UUID id = UUID.randomUUID();
    String filename = "cv.pdf";
    byte[] fileBytes = "fake-pdf-bytes".getBytes();
    String extractedText = "John Doe is a software engineer.";
    NameInfo expectedNameInfo = new NameInfo("John", "Doe");

    when(blobRepository.downloadFile(filename)).thenReturn(fileBytes);
    when(nameExtractionService.extractTextFromFile(fileBytes)).thenReturn(extractedText);
    when(nameExtractionService.findNameInText(extractedText)).thenReturn(expectedNameInfo);

    NameInfo result = cvParserService.extractName(id, filename);

    assertEquals(expectedNameInfo, result);
    verify(blobRepository, times(1)).downloadFile(filename);
    verify(nameExtractionService, times(1)).extractTextFromFile(fileBytes);
    verify(nameExtractionService, times(1)).findNameInText(extractedText);
    verify(messageProducer, times(1)).sendNameInfoEvent(expectedNameInfo, id);
  }
}
