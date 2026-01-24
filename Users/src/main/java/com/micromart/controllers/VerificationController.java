package com.micromart.controllers;

import com.micromart.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class VerificationController {

    private final UserService userService;
    @GetMapping("/verify")
    public ResponseEntity<Object> verifyUser(@RequestParam("token") String token) {
        System.out.println("✅ VerificationController called with token: " + token);
        boolean isVerified = userService.verifyUser(token);
        if (isVerified) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/employees/verification_success.html"))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/employees/verification_failure.html"))
                    .build();
        }
    }
}
