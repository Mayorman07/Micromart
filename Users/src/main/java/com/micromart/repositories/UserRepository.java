package com.micromart.repositories;

import com.micromart.constants.Status;
import com.micromart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findByVerificationToken(String token);
    List<User> findAllByStatusAndLastLoggedInBefore(Status status, Date cutoffDate);
    Optional<User> findByPasswordResetToken(String token);
    @Query("SELECT u FROM User u WHERE " +
            "u.lastLoggedIn < :thirtyDaysAgo AND " +
            "u.lastLoggedIn IS NOT NULL AND " +
            "(u.lastReactivationEmailSentDate IS NULL OR u.lastReactivationEmailSentDate < :ninetyDaysAgo)")
    List<User> findUsersForReactivation(@Param("thirtyDaysAgo") Date thirtyDaysAgo,
                                        @Param("ninetyDaysAgo") Date ninetyDaysAgo);
}
