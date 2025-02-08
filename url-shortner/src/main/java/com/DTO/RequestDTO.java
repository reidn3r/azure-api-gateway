package com.DTO;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {

    @NotNull
    @NotBlank
    @URL
    private String url;

    @NotNull
    @Positive
    private int expiresIn;

}
