package com.micromart.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest {
    @Size(min = 2, message = "First name can't be less than two characters")
    private String firstName;
    @Size(min = 2, message = "Last name can't be less than two characters")
    private String lastName;
    @Email
    private String email;
    @Size(min = 5, max = 16, message = "Password must be between 3 and 12 characters!")
    private String password;
    private String gender;
    @Size(min = 11, max = 15, message = "Mobile number must be between 11 and 15 digits")
    private String mobileNumber;
    private AddressRequest address;
}