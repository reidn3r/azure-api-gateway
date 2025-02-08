package com.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JsonFileDTO {

    private String sourceURL;
    private String destinyUrl;
    private LocalDateTime expiresDate;
}
