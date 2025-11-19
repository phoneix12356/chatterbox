package chatterbox.utils;

import chatterbox.entities.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtility {

    @Value("${jwt.secret-key}")
    private String secreteKey ;
    @Value("${jwt.access-token-expiration-time}")
    private Long expirationTime;

    public String generateToken(UserDetails user){
        System.out.println("insidee generateTokenFunction + " + user.getUsername());
        String email = (user instanceof Users) ? ((Users) user).getEmail(): user.getUsername();
        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(getExpirationDate()).signWith(getSigningKey()).compact();
    }

    public Date getExpirationDate(){
      Long expiryTime = System.currentTimeMillis() + expirationTime;
      return new Date(expiryTime);
    }

    public Key getSigningKey(){
        byte [] decodedKey  = Base64.getDecoder().decode(secreteKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
    public boolean isTokenExpired(String token){
        Date expirationDate = getClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }
    public Claims getClaims(String token){
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public String extractEmail(String token){
        Claims claim = getClaims(token);
        String email =  claim.getSubject();
        return email;
    }

    public boolean validateToken(String token , UserDetails userDetails){
        if(token == null)return false;
        String extractedEmail = extractEmail(token);
        String email = (userDetails instanceof Users) ? ((Users) userDetails).getEmail(): userDetails.getUsername();
        return extractedEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
