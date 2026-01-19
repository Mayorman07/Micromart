package com.micromart.entities;

import com.micromart.constants.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -273145678149216053L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 25)
    private String firstName;
    @Column(nullable = false, length = 25)
    private String lastName;
    @Column(nullable = false, length = 50,unique = true)
    private String email;
    @Column(nullable = false,unique = true)
    private String userId;
    @Column(nullable = false)
    private String encryptedPassword;
    private String gender;
    private Date lastLoggedIn;
    private Date lastPasswordResetDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(name = "verification_token")
    private String verificationToken;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    @Column(name = "password_reset_token")
    private String passwordResetToken;
    @Column(name = "password_reset_token_expiry_date")
    private Date passwordResetTokenExpiryDate;
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    @ManyToMany(cascade = CascadeType.PERSIST ,fetch = FetchType.EAGER )
    @JoinTable(name="users_roles", joinColumns =@JoinColumn(name = "user_id", referencedColumnName = "id"),inverseJoinColumns
            = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    Collection<Role> roles;

    @PrePersist
    public void beforeSave() {
        this.createdAt = new Timestamp(new Date().getTime());
    }

    @PreUpdate
    private void beforeUpdate() {
        this.updatedAt = new Timestamp(new Date().getTime());
    }
}
