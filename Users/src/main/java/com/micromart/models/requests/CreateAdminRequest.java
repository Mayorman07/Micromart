package com.micromart.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAdminRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private String department;
}
