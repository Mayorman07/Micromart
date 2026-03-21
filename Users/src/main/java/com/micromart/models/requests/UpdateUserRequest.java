package com.micromart.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.micromart.validation.SafeText;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @SafeText(message = "First name contains invalid or malicious characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @SafeText(message = "Last name contains invalid or malicious characters")
    private String lastName;

    @Email(message = "Must be a well-formed email address")
    private String email;

    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid gender selection")
    private String gender;

    @Pattern(regexp = "^\\+?[0-9]{11,15}$", message = "Mobile number must be between 11 and 15 digits")
    private String mobileNumber;

    @Valid
    private AddressRequest address;
}