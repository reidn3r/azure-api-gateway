package com.create;

import com.DTO.ExceptionDTO;
import com.DTO.JsonUrlFileDTO;
import com.DTO.RequestDTO;
import com.providers.StorageProvider;
import com.providers.URLProvider;
import com.utils.ValidatorUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
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

public class Function {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final StorageProvider storageProvider = new StorageProvider();
  private final URLProvider urlProvider = new URLProvider(storageProvider, objectMapper);
  private final ValidatorUtils validatorUtils = new ValidatorUtils();

  @FunctionName("create")
  public HttpResponseMessage run(
    @HttpTrigger(
      name = "req",
      methods = {HttpMethod.POST},
      authLevel = AuthorizationLevel.ANONYMOUS
    ) HttpRequestMessage<Optional<String>> request,
    final ExecutionContext context) throws Exception {
    try {
      String body = this.validateAndGetBody(request);
      RequestDTO requestDTO = this.objectMapper.readValue(body, RequestDTO.class);

      //Retorna exception em caso de erros
      this.validateRequestDTO(requestDTO);
      
      JsonUrlFileDTO response = this.urlProvider.createUrl(requestDTO);
      return this.createJsonResponse(request, HttpStatus.CREATED, response);

    } catch (Exception e) {
      return this.createJsonResponse(request, HttpStatus.OK, e.getMessage());
    }
  }

  private String validateAndGetBody(HttpRequestMessage<Optional<String>> request) throws Exception {
    Optional<String> body = request.getBody();
    Optional<ExceptionDTO> validation = validatorUtils.validateStringBody(body);
    
    if (validation.isPresent())
      throw new Exception("Erro ao validar Payload: " + validation.toString());

    if(body.isEmpty()) 
      throw new Exception("Corpo da requisição não pode ser nulo");

    return body.get();
  }

  private void validateRequestDTO(RequestDTO requestDTO) throws Exception {
    Optional<ExceptionDTO> validation = validatorUtils.validate(requestDTO);
    if (validation.isPresent()) {
      throw new Exception(validation.get().toString());
    }
  }

  private <T> HttpResponseMessage createJsonResponse(
    HttpRequestMessage<?> request,
    HttpStatus status,
    T body) throws JsonProcessingException {
  
    String json = objectMapper.writeValueAsString(body);
    return request.createResponseBuilder(status)
      .header("Content-Type", "application/json")
      .body(json)
      .build();
  }

}
