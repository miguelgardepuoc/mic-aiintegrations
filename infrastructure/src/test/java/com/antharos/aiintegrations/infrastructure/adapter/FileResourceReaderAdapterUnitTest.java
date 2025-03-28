package com.antharos.aiintegrations.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class FileResourceReaderAdapterTest {

  @Test
  void readNamesFromResource_ValidResourceFile_ReturnsCorrectNames() throws IOException {
    // Arrange
    String content = """
            John Doe
            Jane Smith
              Alice Johnson \s
            
            Bob Brown
            """;

    FileResourceReaderAdapter resourceReader = createMockedResourceReader(content);

    // Act
    Set<String> names = resourceReader.readNamesFromResource("test-resource.txt");

    // Assert
    assertEquals(4, names.size());
    assertTrue(names.contains("john doe"));
    assertTrue(names.contains("jane smith"));
    assertTrue(names.contains("alice johnson"));
    assertTrue(names.contains("bob brown"));
  }

  @Test
  void readNamesFromResource_EmptyResourceFile_ReturnsEmptySet() throws IOException {
    // Arrange
    FileResourceReaderAdapter resourceReader = createMockedResourceReader("");

    // Act
    Set<String> names = resourceReader.readNamesFromResource("test-resource.txt");

    // Assert
    assertTrue(names.isEmpty());
  }

  @Test
  void readNamesFromResource_OnlyWhitespaceAndNewlines_ReturnsEmptySet() throws IOException {
    // Arrange
    String content = """
              \s
            
                 \t  \s
            """;

    FileResourceReaderAdapter resourceReader = createMockedResourceReader(content);

    // Act
    Set<String> names = resourceReader.readNamesFromResource("test-resource.txt");

    // Assert
    assertTrue(names.isEmpty());
  }

  @Test
  void readNamesFromResource_ResourceFileWithDuplicates_ReturnsUniqueNames() throws IOException {
    // Arrange
    String content = """
            John Doe
            john doe
            JOHN DOE
            Jane Smith
            jane smith
            """;

    FileResourceReaderAdapter resourceReader = createMockedResourceReader(content);

    // Act
    Set<String> names = resourceReader.readNamesFromResource("test-resource.txt");

    // Assert
    assertEquals(2, names.size());
    assertTrue(names.contains("john doe"));
    assertTrue(names.contains("jane smith"));
  }

  @Test
  void readNamesFromResource_NonExistentResourceFile_ThrowsIOException() {
    // Arrange
    FileResourceReaderAdapter resourceReader = new FileResourceReaderAdapter();
    String nonExistentPath = "non_existent_file.txt";

    // Act & Assert
    assertThrows(
        IOException.class,
        () -> {
          resourceReader.readNamesFromResource(nonExistentPath);
        });
  }

  // Helper method to create a mocked ResourceReader with predefined content
  private FileResourceReaderAdapter createMockedResourceReader(String content) throws IOException {
    // Create a spy of the actual implementation
    FileResourceReaderAdapter spyReader = spy(new FileResourceReaderAdapter());

    // Create a mock ClassPathResource
    ClassPathResource mockResource = mock(ClassPathResource.class);

    // Setup the input stream for the mock resource
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

    // Stub the resource creation and input stream methods
    when(mockResource.getInputStream()).thenReturn(inputStream);
    doReturn(mockResource).when(spyReader).createClassPathResource(anyString());

    return spyReader;
  }
}
