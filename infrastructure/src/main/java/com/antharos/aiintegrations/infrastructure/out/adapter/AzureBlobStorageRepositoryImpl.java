package com.antharos.aiintegrations.infrastructure.out.adapter;

import com.antharos.aiintegrations.domain.repository.BlobRepository;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AzureBlobStorageRepositoryImpl implements BlobRepository {

  @Value("${azure.storage.connection-string}")
  private String connectionString;

  @Value("${azure.storage.container-name}")
  private String containerName;

  @Override
  public byte[] downloadFile(String cvFilename) {
    BlobContainerClient containerClient =
        new BlobContainerClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .buildClient();

    BlobClient blobClient = containerClient.getBlobClient(cvFilename);

    try (InputStream inputStream = blobClient.openInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      inputStream.transferTo(outputStream);
      return outputStream.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Failed to download blob: " + cvFilename, e);
    }
  }
}
