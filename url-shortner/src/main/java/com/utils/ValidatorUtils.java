package com.utils;

import java.util.Optional;
import java.util.Set;

import com.DTO.ExceptionDTO;
import com.DTO.RequestDTO;
import com.microsoft.azure.functions.HttpStatus;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidatorUtils {
  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  public Validator getValidator() {
    return validator;
  }

  public Optional<ExceptionDTO> validateStringBody(Optional<String> data) {
    if (data.isEmpty()) {
      String message = "url e expiresIn são obrigatórios";
      ExceptionDTO exception = new ExceptionDTO(message, HttpStatus.INTERNAL_SERVER_ERROR);
      Optional<ExceptionDTO> result = Optional.ofNullable(exception);
      return result;
    }
    return Optional.empty();
  }

  public Optional<ExceptionDTO> validate(RequestDTO data) {
    Validator validator = this.getValidator();
    Set<ConstraintViolation<RequestDTO>> violations = validator.validate(data);

    if (!violations.isEmpty()) {
      String errorMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .reduce((m1, m2) -> m1 + "; " + m2)
        .orElse("Dados inválidos");

      Optional<ExceptionDTO> exception = Optional
        .ofNullable(new ExceptionDTO(errorMessages, HttpStatus.BAD_REQUEST));
      return exception;
    }
    return Optional.empty();
  }
}
