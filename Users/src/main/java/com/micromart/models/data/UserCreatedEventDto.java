package com.micromart.models.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEventDto implements Serializable {
    private String firstName;
    private String email;
    private String verificationToken;
    private String type;
}