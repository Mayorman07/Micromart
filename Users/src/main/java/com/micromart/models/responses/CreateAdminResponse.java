package com.micromart.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private List<String> roles;
    private String userId;
    private String username;
    private Date lastLoggedIn;
    private String createdAt;
    private String updatedAt;
}
