package com.micromart.utils;

package com.micromart.services;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    private static final int DEFAULT_EXPIRY_MINUTES = 15;

    /**
     * Generates a random, unique string for use as a token.
     */
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Calculates an expiry date starting from NOW.
     * @param expiryTimeInMinutes How many minutes the token should last.
     */
    public Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.getTime();
    }

    /**
     * Overload: Uses the default 15 minutes if no time is specified.
     */
    public Date calculateExpiryDate() {
        return calculateExpiryDate(DEFAULT_EXPIRY_MINUTES);
    }

    /**
     * Checks if a token is expired.
     * Returns TRUE if the date has passed.
     */
    public boolean isTokenExpired(Date expiryDate) {
        return expiryDate != null && expiryDate.before(new Date());
    }
}