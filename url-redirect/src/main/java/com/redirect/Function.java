package com.redirect;

import com.DTO.ExceptionDTO;
import com.DTO.JsonFileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.providers.StorageProvider;

import java.time.LocalDateTime;
import java.util.Optional;

public class Function {
  private final ObjectMapper objectMapper = new ObjectMapper();
  public StorageProvider storageProvider = new StorageProvider(objectMapper);

  @FunctionName("redirect")
  public HttpResponseMessage run(
    @HttpTrigger(
      name = "req",
      methods = {HttpMethod.GET},
      authLevel = AuthorizationLevel.ANONYMOUS,
      route = "{id}")
    HttpRequestMessage<Optional<String>> request,
    @BindingName("id") String id,
    final ExecutionContext context
  ) throws Exception {

  try {
    this.checkId(id);
    
    Optional<String> destinyUrl = this.getRedirectURL(id);
    this.checkDestinyUrl(destinyUrl);

    return this.createRedirectResponse(request, HttpStatus.FOUND, destinyUrl.get());
  } catch (Exception e) {
    ExceptionDTO exception = new ExceptionDTO(e.getMessage(), HttpStatus.NOT_FOUND);
    return this.createResponse(request, HttpStatus.NOT_FOUND, exception);
  }
}

  private Optional<String> getRedirectURL(String id) throws Exception {
    JsonFileDTO foundFile = this.storageProvider.findBlobByName(id);

    return foundFile.getExpiresDate().isAfter(LocalDateTime.now())
      ? Optional.ofNullable(foundFile.getSourceURL())
      : Optional.empty();
  }

  private void checkId(String id) throws Exception {
    if (id == null || id.isBlank())
      throw new Exception("URL not found");
  }

  private void checkDestinyUrl(Optional<String> url) throws Exception {
    if (url.isEmpty())
      throw new Exception("URL expired");
  }

  private <T> HttpResponseMessage createRedirectResponse(HttpRequestMessage<T> request, HttpStatus status, String url) {
    return request.createResponseBuilder(HttpStatus.FOUND)
      .header("Location", url)
      .build();
  }
  
  private <T> HttpResponseMessage createResponse(HttpRequestMessage<T> request, HttpStatus status, Object body) {
    return request.createResponseBuilder(HttpStatus.FOUND)
      .body(body)
      .build();
  }
}
