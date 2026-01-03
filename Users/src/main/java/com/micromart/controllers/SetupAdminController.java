package com.micromart.controllers;

import com.micromart.exceptions.NotFoundException;
import com.micromart.models.data.UserDto;
import com.micromart.models.requests.CreateAdminRequest;
import com.micromart.models.responses.CreateAdminResponse;
import com.micromart.services.UserService;
import com.micromart.services.UserServiceImpl;
import com.micromart.validations.InputValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupAdminController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @PostMapping("/create-admin")
    public ResponseEntity<CreateAdminResponse> createInitialAdmin(@Valid @RequestBody CreateAdminRequest request, BindingResult bindingResult){
        logger.info("The incoming create employee request {} " , request);
        InputValidator.validate(bindingResult);
        try {
            UserDto adminDto = modelMapper.map(request, UserDto.class);
            UserDto createdAdminDto = userService.createInitialAdmin(adminDto);
            CreateAdminResponse returnValue= modelMapper.map(createdAdminDto,CreateAdminResponse.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
        }
        catch (IllegalStateException e) {
            // This error is thrown if the endpoint is called a second time
            CreateAdminResponse errorResponse = new CreateAdminResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }catch (NotFoundException e) {
            CreateAdminResponse errorResponse = new CreateAdminResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
