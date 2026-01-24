package com.micromart.models.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEventDto implements Serializable {
    private String email;
    private String firstName;
    private String passwordResetToken;
}
