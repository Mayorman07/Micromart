package com.micromart.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEventDto implements Serializable {
    private String firstName;
    private String email;
    private String verificationToken;
    private String mobileNumber;
    private String type;
}