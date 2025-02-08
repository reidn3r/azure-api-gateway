package com.providers;

import java.time.LocalDateTime;

import com.DTO.JsonUrlFileDTO;
import com.DTO.RequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class URLProvider {

    private final ObjectMapper objectMapper;
    private StorageProvider storageProvider;
    private String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
    private int urlSize = 5;

    public URLProvider(StorageProvider storageProvider, ObjectMapper objectMapper) {
        this.storageProvider = storageProvider;
        this.objectMapper = objectMapper;
        this.objectMapper
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public JsonUrlFileDTO createUrl(RequestDTO data) throws JsonProcessingException {
        String newStringUrl = this.buildStringUrl(this.urlSize);
        boolean urlExists = this.checkIfUrlIsCreated(newStringUrl);

        while (urlExists) {
            newStringUrl = this.buildStringUrl(this.urlSize);
            urlExists = this.checkIfUrlIsCreated(newStringUrl);
        }

        LocalDateTime expiresDate = LocalDateTime.now().plusSeconds(data.getExpiresIn());
        JsonUrlFileDTO newJson = new JsonUrlFileDTO(data.getUrl(), newStringUrl, expiresDate);
        String jsonString = objectMapper.writeValueAsString(newJson);
        this.storageProvider.uploadJSON(jsonString, storageProvider.containerName, newStringUrl);

        return newJson;
    }

    public boolean checkIfUrlIsCreated(String URL) {
        String blobName = URL + ".json";
        return storageProvider.blobExistsByName(blobName, storageProvider.containerName);
    }

    public String buildStringUrl(int size) {
        StringBuffer buffer = new StringBuffer("");
        int charIndex;
        for (int i = 0; i < size; i++) {
            charIndex = (int) (Math.random() * 1000) % AlphaNumericString.length();
            buffer.append(AlphaNumericString.charAt(charIndex));
        }

        return buffer.toString();
    }
}
