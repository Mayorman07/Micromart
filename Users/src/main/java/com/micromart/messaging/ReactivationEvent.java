package com.micromart.messaging;

public class ReactivationEvent {

    private String email;
    public PasswordResetRequestEvent() {}
    public PasswordResetRequestEvent(String email) {
        this.email = email;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
