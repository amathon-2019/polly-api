package com.example.polly.PollyDemo.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.polly.PollyDemo.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFactory {
    private static final String CLAIM_NAME_MEMBER_ID = "id";

    private final String tokenIssuer;
    private final String tokenSigningKey;
    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(tokenSigningKey);
        jwtVerifier = JWT.require(algorithm).build();
    }

    public String generateToken(Integer memberId) {
        return JWT.create()
                .withIssuer(tokenIssuer)
                .withClaim(CLAIM_NAME_MEMBER_ID, memberId)
                .sign(algorithm);
    }

    public Optional<Long> decodeToken(String authorizationHeader) {
        try {
            final String token = this.extractAccessToken(authorizationHeader);
            final DecodedJWT decodedJWT = jwtVerifier.verify(token);
            final Map<String, Claim> claims = decodedJWT.getClaims();
            final Claim idClaim = claims.get(CLAIM_NAME_MEMBER_ID);
            if (idClaim == null) {
                log.warn("Failed to decode jwt token. header:" + authorizationHeader);
                return Optional.empty();
            }
            return Optional.of(idClaim.asLong());
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to extract token from header. header:" + authorizationHeader, ex);
            return Optional.empty();
        } catch (JWTVerificationException ex) {
            log.warn("Invalid access Token. header:" + authorizationHeader, ex);
            return Optional.empty();
        }
    }

    private String extractAccessToken(String header) {
        if (StringUtils.isEmpty(header)) {
            throw new IllegalArgumentException("Invalid authorization header. header:" + header);
        }
        return header;
    }
}

