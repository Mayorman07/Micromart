package com.micromart.messaging;

import java.io.Serializable;

public class PasswordResetRequestEvent implements Serializable {
    private String email;
    public PasswordResetRequestEvent() {}
    public PasswordResetRequestEvent(String email) {
        this.email = email;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
