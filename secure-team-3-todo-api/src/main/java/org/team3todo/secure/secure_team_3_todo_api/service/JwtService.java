package org.team3todo.secure.secure_team_3_todo_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // For injecting properties
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import jakarta.annotation.PostConstruct; // For @PostConstruct
import java.io.IOException; // Still needed for some method signatures as per original
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.expiration-time-ms:86400000}")
    private long expirationTimeMs;

    @Value("${jwt.secret.private-key-base64}")
    private String privateKeyBase64String;

    @Value("${jwt.secret.public-key-base64}")
    private String publicKeyBase64String;

    private PrivateKey signingKeyInstance;
    private PublicKey publicKeyInstance;

    @PostConstruct
    public void initKeys() {
        try {

            if (privateKeyBase64String == null || privateKeyBase64String.isEmpty() ||
                publicKeyBase64String == null || publicKeyBase64String.isEmpty()) {
                throw new RuntimeException("JWT key configuration is missing in application properties. " +
                                           "Please set jwt.secret.private-key-base64 and jwt.secret.public-key-base64.");
            }

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64String);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            this.signingKeyInstance = rsaKeyFactory.generatePrivate(privateKeySpec);

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64String);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKeyInstance = rsaKeyFactory.generatePublic(publicKeySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Could not initialize JWT keys due to algorithm or key spec error.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 format for JWT keys in application properties.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error initializing JWT keys.", e);
        }
    }

    /**
     * Extracts the userGuid (subject) from the JWT token.
     * @param token The JWT token.
     * @return The userGuid.
     */
    public UUID extractUserGuid(String token) {
        UUID userGuid = UUID.fromString(extractClaim(token, Claims::getSubject));
        return userGuid;
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     * @param token The JWT token.
     * @param claimsResolver A function to extract the desired claim.
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) {
            throw new RuntimeException("Failed to extract claims, cannot resolve specific claim.");
        }
        T claim = claimsResolver.apply(claims);
        return claim;
    }

    /**
     * Parses the JWT token and extracts all claims using the public key for verification.
     * @param token The JWT token.
     * @return The claims contained in the token.
     * @throws RuntimeException if there's an issue parsing or verifying the token.
     */
    private Claims extractAllClaims(String token) {
        try {
            if (this.publicKeyInstance == null) {
                 throw new RuntimeException("Public key has not been initialized. Check configuration.");
            }
            Claims claims = Jwts.parser()
                    .verifyWith(this.publicKeyInstance) 
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims;
        } catch (Exception e) {
            throw new RuntimeException("Invalid token or unable to parse claims.", e);
        }
    }

    private PrivateKey getSigningKey() {
        if (this.signingKeyInstance == null) {
            throw new IllegalStateException("Signing key has not been initialized.");
        }
        return this.signingKeyInstance;
    }

    /**
     * Generates a JWT token.
     * @param extraClaims Additional claims.
     * @param userDetails User details.
     * @return The generated JWT token.
     * @throws InvalidKeyException If key is invalid.
     * @throws IOException If key loading fails (though less likely with @PostConstruct init)
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) throws InvalidKeyException, IOException {
        if (extraClaims == null || !extraClaims.containsKey("userGuid") || extraClaims.get("userGuid") == null) {
            throw new IllegalArgumentException("'userGuid' must be provided in extraClaims to generate a token.");
        }
        String token = buildToken(extraClaims, userDetails, this.expirationTimeMs); 
        return token;
    }

    /**
     * Builds the JWT token.
     * @param extraClaims Additional claims.
     * @param userDetails User details.
     * @param expirationMillis Expiration time.
     * @return The compact JWT string.
     * @throws InvalidKeyException If key is invalid.
     * @throws IOException (kept for compatibility)
     */
    public String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationMillis) throws InvalidKeyException, IOException {
        String token = Jwts.builder()
                .subject(extraClaims.get("userGuid").toString())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .collect(Collectors.toList()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), Jwts.SIG.RS256)
                .compact();
        return token;
    }

    /**
     * Validates the JWT token.
     * @param token The JWT token.
     * @param userDetails User details.
     * @return True if valid, false otherwise.
     */
    public boolean isTokenValid(String token, User userDetails) {
        try {
            final UUID userGuid = extractUserGuid(token);
            if (!userGuid.equals(userDetails.getUserGuid())) {
                return false;
            }
            if (isTokenExpired(token)) {
                return false;
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        
        return expired;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}