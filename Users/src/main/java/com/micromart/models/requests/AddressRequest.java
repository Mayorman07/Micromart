package com.micromart.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.micromart.validation.SafeText;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressRequest {
    @NotBlank(message = "Street cannot be empty")
    @SafeText(message = "Street contains invalid or malicious characters")
    private String street;

    @NotBlank(message = "City cannot be empty")
    @SafeText(message = "City contains invalid or malicious characters")
    private String city;

    @SafeText(message = "State contains invalid or malicious characters")
    private String state;

    @NotBlank(message = "Country cannot be empty")
    @SafeText(message = "Country contains invalid or malicious characters")
    private String country;

    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Zip code contains invalid characters")
    private String zipCode;
}
