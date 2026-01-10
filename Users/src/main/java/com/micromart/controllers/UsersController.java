package com.micromart.controllers;

import com.micromart.models.data.UserDto;
import com.micromart.models.data.UserProfileDto;
import com.micromart.models.requests.CreateUserRequest;
import com.micromart.models.responses.CreateUserResponse;
import com.micromart.models.responses.UserProfileResponse;
import com.micromart.services.UserService;
import com.micromart.validations.InputValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest, BindingResult bindingResult){
        logger.info("The incoming create employee request {} " , userRequest);
        InputValidator.validate(bindingResult);
        UserDto userDto = modelMapper.map(userRequest, UserDto.class);
        UserDto createdUserDto = userService.createUser(userDto);
        CreateUserResponse returnValue = modelMapper.map(createdUserDto,CreateUserResponse.class);
        logger.info("The outgoing create employee response {} " , returnValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('product:READ')")
    public ResponseEntity<CreateUserResponse> updateUser(@Valid @RequestBody CreateUserRequest updateUserRequest){
        logger.info("The incoming update user request {} " , updateUserRequest);
    UserDto userDto = modelMapper.map(updateUserRequest, UserDto.class);
    UserDto userToBeUpdated = userService.updateUser(userDto);
    CreateUserResponse returnValue = modelMapper.map(userToBeUpdated, CreateUserResponse.class);
        logger.info("The outgoing update employee response {} " , returnValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    @PutMapping("/{userId}/roles/manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignManagerRole(@PathVariable String userId) {
        userService.assignManagerRole(userId);
        return ResponseEntity.ok("Employee has been promoted to manager successfully.");
    }
    @DeleteMapping(path ="/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email){
        logger.info("The incoming request to delete a user {}", email);
        userService.deleteUser(email);
        logger.info("Employee with email {} deleted successfully.", email);
        return ResponseEntity.noContent().build();
    }

    public void deactivateUsers( @PathVariable String email){
        logger.info("The incoming deactivate user request {} " , email);
        userService.deactivateUser(email);
        logger.info("User with email {} has been deactivated successfully.", email);
    }

    @GetMapping(path ="/view/{email}")
    @PreAuthorize("hasAuthority('product:READ')")
    public ResponseEntity<UserProfileResponse> viewProfile(@PathVariable("email") String email){
        logger.info("Received request to view personal employee with email: {}", email);
        UserProfileDto requestedUserDetails = userService.viewProfile(email);
        UserProfileResponse returnValue = modelMapper.map(requestedUserDetails,UserProfileResponse.class);
        return ResponseEntity.ok(returnValue);
    }

}
