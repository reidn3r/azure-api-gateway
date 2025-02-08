package com.createUrl;

import com.DTO.ExceptionDTO;
import com.DTO.JsonUrlFileDTO;
import com.DTO.RequestDTO;
import com.providers.StorageProvider;
import com.providers.URLProvider;
import com.utils.ValidatorUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StorageProvider storageProvider = new StorageProvider();
    private final URLProvider urlProvider = new URLProvider(storageProvider, objectMapper);
    private final ValidatorUtils validatorUtils = new ValidatorUtils();

    @FunctionName("create")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {

        Optional<String> body = request.getBody();
        Optional<ExceptionDTO> bodyValidation = validatorUtils.validateStringBody(body);
        if (!bodyValidation.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(bodyValidation.get()))
                    .build();
        }

        try {
            RequestDTO requestDTO = objectMapper.readValue(body.get(), RequestDTO.class);

            Optional<ExceptionDTO> requestDTOValidation = validatorUtils.validate(requestDTO);
            if (!requestDTOValidation.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(requestDTOValidation.get()))
                        .build();
            }

            JsonUrlFileDTO response = urlProvider.createUrl(requestDTO);

            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(response))
                    .build();
        } catch (Exception e) {
            ExceptionDTO exception = new ExceptionDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(exception))
                    .build();
        }
    }
}