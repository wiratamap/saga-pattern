package com.personal.sagapattern.authentication.jwt;

import com.personal.sagapattern.authentication.model.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${authentication.app.jwt.secret}")
    private String jwtSecret;

    @Value("${authentication.app.jwt.expiration}")
    private int jwtExpiration;

    public String generateJwtToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("email", userPrinciple.getEmail());
        userDetails.put("cif", userPrinciple.getCif());
        userDetails.put("name", userPrinciple.getName());

        return Jwts.builder().setSubject(userPrinciple.getUsername()).setIssuedAt(new Date())
                .setClaims(userDetails)
                .setExpiration(new Date(new Date().getTime() + (jwtExpiration * 1000)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    String getUsernameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    boolean isJwtTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {0} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {0}", e);
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token -> Message: {0}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {0}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {0}", e);
        }

        return false;
    }
}
