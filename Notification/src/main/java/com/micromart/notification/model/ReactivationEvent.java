package com.micromart.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReactivationEvent implements Serializable {

    private String email;
    private String firstName;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @Override
    public String toString() {
        return "ReactivationEvent{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
