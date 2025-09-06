package com.DTO;

import com.microsoft.azure.functions.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDTO {
  private String message;
  private HttpStatus status;
}
