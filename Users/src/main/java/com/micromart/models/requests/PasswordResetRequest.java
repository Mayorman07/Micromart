package com.micromart.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordResetRequest {
    private String email;
}
