package com.providers;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.config.EnvironmentConfig;

public class StorageProvider {
  public BlobServiceClient client;
  public String containerName = "url-temp-file";

  public StorageProvider() {
    String endpoint = EnvironmentConfig.get("STORAGE_ENDPOINT");
    String connString = EnvironmentConfig.get("CONN_STRING");
    this.client = new BlobServiceClientBuilder()
      .endpoint(endpoint)
      .connectionString(connString)
      .buildClient();
  }

  public boolean blobExistsByName(String blobName, String containerName) {
    BlobContainerClient foundContainer = this.client.getBlobContainerClient(containerName);
    BlobClient foundBlob = foundContainer.getBlobClient(blobName);
    return foundBlob.exists();
  }

  public void uploadJSON(String data, String containerName, String URL) {
    String blobName = URL + ".json";
    this.client
      .getBlobContainerClient(containerName)
      .getBlobClient(blobName)
      .upload(BinaryData.fromString(data));
  }
}
