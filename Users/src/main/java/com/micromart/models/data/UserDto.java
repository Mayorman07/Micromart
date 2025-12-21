package com.micromart.models.data;

import com.micromart.constants.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    private  static final long serialVersionUID = -953297098295050686L;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String mobileNumber;
    private String userId;
    private String encryptedPassword;
    private String gender;
    private String username;
    private String createdAt;
    private Status status;
    private List<String> roles;
}
