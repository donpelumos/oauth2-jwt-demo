package com.example.oauth2jwtdemo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Pelumi on 11-Jul-2020
 */
@Service
public class JwtTokenUtil {
    @Value("${jwt.signing.key:secret}")
    private String SECRET_KEY;
    private final long JWT_TOKEN_VALIDITY = 15 * 60;//15 Minutes

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * While creating the token -
     * 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
     * 2. Sign the JWT using the HS512 algorithm and secret key.
     * 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     *  compaction of the JWT to a URL-safe string
     * @param claims
     * @param subject
     * @return
     */
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
    }

    private String createVerifiedToken(Map<String, Object> claims, String subject) throws UnsupportedEncodingException {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(SECRET_KEY.getBytes("UTF-8"))).compact();
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public String generateVerifiedToken(UserDetails userDetails) throws UnsupportedEncodingException {
        Map<String, Object> claims = new HashMap<>();
        return createVerifiedToken(claims, userDetails.getUsername());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
