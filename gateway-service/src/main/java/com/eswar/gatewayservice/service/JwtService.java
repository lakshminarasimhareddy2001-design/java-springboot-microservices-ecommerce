package com.eswar.gatewayservice.service;


import com.eswar.gatewayservice.exceptions.BusinessException;
import com.eswar.gatewayservice.exceptions.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;


    public void validateToken(String token) {

        try {
            extractAllClaims(token);

            if (isTokenExpired(token)) {
                throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
            }

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);

        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            throw new BusinessException(ErrorCode.TOKEN_MALFORMED);

        } catch (io.jsonwebtoken.SignatureException ex) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);

        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }
    public String extractUserId(String token) {

        return extractAllClaims(token).getSubject();
    }
    public String extractUserEmail(String token) {

        return extractAllClaims(token).get("email",String.class);
    }

    public List<String> extractUserRoles(String token) {
            Claims claims=extractAllClaims(token);
        return ((List<?>)claims.get("roles",List.class)).stream().map(Object::toString).toList();
    }



    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
