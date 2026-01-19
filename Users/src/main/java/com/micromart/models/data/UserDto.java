package com.micromart.models.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.micromart.constants.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    @Serial
    private  static final long serialVersionUID = -953297098295050686L;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String userId;
    private String encryptedPassword;
    private String gender;
    private String username;
    private String createdAt;
    private Status status;
    private List<String> roles;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AddressDto address;
    private List<AddressDto> addresses;

}
