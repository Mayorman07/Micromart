package com.micromart.Users.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@RefreshScope
public class UsersController{
    @GetMapping(path = "/test/status")
    public String status(){
     return "Just testing as usual";
    }

}
