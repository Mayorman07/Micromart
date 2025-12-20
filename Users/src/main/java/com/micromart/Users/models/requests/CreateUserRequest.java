package com.micromart.Users.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest {
    @NotNull(message = "First name cannot be null")
    @Size(min = 2, message = "First name cant be less than two characters")
    @NotNull(message = "First name cannot be null")
    private String firstName;
    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, message = "Last name cant be less than two characters")
    private String lastName;
    @Email
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 3, max = 12, message = "Password must be between 3 and 12 characters!")
    private String password;
    private String status;
    @NotNull(message = "Gender field cannot be null")
    @NotEmpty(message = "Gender field cannot be empty")
    private String gender;
    @NotNull(message = "Address field cannot be null")
    @NotEmpty(message = "Address field cannot be empty")
    private String address;
    @NotNull(message = "mobileNumber cannot be null")
    @Size(min = 11,max = 15, message = "mobileNumber must be between 11 and 15 numbers")
    private int mobileNumber;
}