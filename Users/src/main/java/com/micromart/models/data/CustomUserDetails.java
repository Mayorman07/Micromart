package com.micromart.models.data;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

//    private final String employeeId;
//    private final String department;

//    public CustomUserDetails(String username, String password, boolean enabled,
//                             boolean accountNonExpired, boolean credentialsNonExpired,
//                             boolean accountNonLocked,
//                             Collection<? extends GrantedAuthority> authorities,
//                             String employeeId, String department) {
//        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//        this.employeeId = employeeId;
//        this.department = department;
//    }

    //not sure there should be String department though

    public CustomUserDetails(String username, String password, boolean enabled,
                             boolean accountNonExpired, boolean credentialsNonExpired,
                             boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

}