package com.micromart.models.responses;

import com.micromart.models.data.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private List<String> roles;
    private String userId;
    private String username;
d     private String createdAt;
    private String updatedAt;
    private AddressDto address;
    private List<AddressDto> addresses;

    public CreateUserResponse(String message) {
    }
}