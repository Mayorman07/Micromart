package com.micromart.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank
    private String street;
    @NotBlank
    private String city;
    private String state;
    @NotBlank
    private String country;
    private String zipCode;
}
