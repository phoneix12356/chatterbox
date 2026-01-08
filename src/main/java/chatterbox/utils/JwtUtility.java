package chatterbox.utils;

import chatterbox.entities.Users;
import chatterbox.services.CustomUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtility {

    @Value("${jwt.secret-key}")
    private String secreteKey;
    @Value("${jwt.access-token-expiration-time}")
    private Long expirationTime;

    public String generateToken(UserDetails user) {
        System.out.println("inside generateTokenFunction + " + user.getUsername());
        String email = user.getUsername();
        if (user instanceof CustomUserDetail) {
            email = ((CustomUserDetail) user).getEmail();
        } else if (user instanceof Users) {
            email = ((Users) user).getEmail();
        }
        return Jwts.builder().subject(email).issuedAt(new Date()).expiration(getExpirationDate())
                .signWith(getSigningKey()).compact();
    }

    public Date getExpirationDate() {
        Long expiryTime = System.currentTimeMillis() + expirationTime;
        return new Date(expiryTime);
    }

    public Key getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secreteKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith((javax.crypto.SecretKey) getSigningKey()).build().parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        Claims claim = getClaims(token);
        return claim.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (token == null)
            return false;
        String extractedEmail = extractEmail(token);
        String userEmail = userDetails.getUsername();
        if (userDetails instanceof CustomUserDetail) {
            userEmail = ((CustomUserDetail) userDetails).getEmail();
        } else if (userDetails instanceof Users) {
            userEmail = ((Users) userDetails).getEmail();
        }
        return extractedEmail.equals(userEmail) && !isTokenExpired(token);
    }
}
