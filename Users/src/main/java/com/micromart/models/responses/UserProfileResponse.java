package com.micromart.models.responses;
import java.util.Date;
import com.micromart.constants.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class UserProfileResponse {

    private String firstName;
    private String lastName;
    private String email;
    private Status status;
    private String userId;
    private Date lastLoggedIn;
}
