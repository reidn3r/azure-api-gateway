package com.providers;

import com.DTO.JsonFileDTO;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.config.EnvironmentConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class StorageProvider {

    private String containerName = "url-temp-file";
    private BlobServiceClient client;
    private ObjectMapper serializer;

    public StorageProvider(ObjectMapper objectMapper) {
        String endpoint = EnvironmentConfig.get("STORAGE_ENDPOINT");
        String connString = EnvironmentConfig.get("CONN_STRING");

        this.serializer = objectMapper.registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.client = new BlobServiceClientBuilder()
                .endpoint(endpoint)
                .connectionString(connString)
                .buildClient();
    }

    public JsonFileDTO findBlobByName(String name) throws Exception {
        String blobName = name + ".json";

        BlobContainerClient containerClient = this.client.getBlobContainerClient(this.containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new Exception("URL not found");
        }

        String fileStream = blobClient.downloadContent().toString();
        return this.serializer.readValue(fileStream, JsonFileDTO.class);
    }

}
