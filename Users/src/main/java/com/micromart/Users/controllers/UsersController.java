package com.micromart.Users.controllers;

import com.micromart.Users.models.data.UserDto;
import com.micromart.Users.models.requests.CreateUserRequest;
import com.micromart.Users.models.responses.CreateUserResponse;
import com.micromart.Users.services.UserService;
import com.micromart.Users.validations.InputValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@RefreshScope
public class UsersController{

    private final UserService userService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @GetMapping(path = "/test/status")
    public String status(){
     return "Just testing as usual";
    }

    public void createUser(CreateUserRequest userRequest, BindingResult bindingResult){
        logger.info("The incoming create employee request {} " , userRequest);
        InputValidator.validate(bindingResult);
        UserDto userDto = modelMapper.map(userRequest, UserDto.class);
        UserDto createdUserDto = userService.createUser(userDto);
        CreateUserResponse returnValue = modelMapper.map(createdUserDto,CreateUserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);



    }

}
