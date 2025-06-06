package org.team3todo.secure.secure_team_3_todo_api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/// A custom PasswordEncoder implementation, we should be using BCryptPasswordEncoder impl, but this allows for pepper
public class CustomPasswordEncoder implements PasswordEncoder {
    private static final String HASH_ALGORITHM = "SHA-256"; 
    private static final int SALT_LENGTH_BYTES = 16;
    private static final String DELIMITER = "$$";
    private final SecureRandom secureRandom;
    private final String pepper; 

    /**
     * Constructor for CustomPasswordEncoder.
     * @param pepper A secret string (pepper) to be combined with passwords before hashing.
     * This should be configured securely and not stored with user data.
     * @throws IllegalArgumentException if the pepper is null or empty.
     */
    public CustomPasswordEncoder(String pepper) {
        if (pepper == null || pepper.isEmpty()) {
            throw new IllegalArgumentException("Pepper cannot be null or empty. Please provide a secure pepper.");
        }
        this.secureRandom = new SecureRandom();
        this.pepper = pepper;
    }

    /**
     * Encodes the raw password.
     * This example generates a salt, combines the raw password with the pepper,
     * then combines that result with the salt, hashes it, and stores the salt and hash together.
     *
     * @param rawPassword The raw password to encode.
     * @return The encoded password (salt + hash representation).
     */
    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null.");
        }

        // 1. Generate a new random salt for each password
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        secureRandom.nextBytes(salt);

        try {
            // 2. Combine rawPassword with the pepper first
            String passwordWithPepper = rawPassword.toString() + pepper;
            byte[] passwordWithPepperBytes = passwordWithPepper.getBytes(StandardCharsets.UTF_8);

            // 3. Combine (password + pepper) with salt
            byte[] finalValueToHash = new byte[passwordWithPepperBytes.length + salt.length];
            System.arraycopy(passwordWithPepperBytes, 0, finalValueToHash, 0, passwordWithPepperBytes.length);
            System.arraycopy(salt, 0, finalValueToHash, passwordWithPepperBytes.length, salt.length);

            // 4. Hash the combined value ( (password+pepper) + salt )
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashedBytes = md.digest(finalValueToHash);

            // 5. Combine salt and hash for storage.
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);

            // Format: saltBase64$$hashBase64
            String encodedResult = saltBase64 + DELIMITER + hashBase64;
            return encodedResult;

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hashing algorithm not found: " + HASH_ALGORITHM, e);
        }
    }

    /**
     * Verifies the encoded password obtained from storage matches the submitted raw
     * password after it too is encoded with the same salt and pepper.
     *
     * @param rawPassword The raw password to encode and match.
     * @param encodedPassword The encoded password from storage to compare against.
     * @return true if the raw password, after encoding, matches the encoded password from storage.
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        String[] parts = encodedPassword.split("\\Q" + DELIMITER + "\\E", 2);
        if (parts.length != 2) {
            return false;
        }

        try {
            // 1. Extract the salt and original hash from the stored encodedPassword
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHashedBytes = Base64.getDecoder().decode(parts[1]);

            // 2. Combine the submitted rawPassword with the SAME pepper used during encoding
            String passwordWithPepper = rawPassword.toString() + pepper;
            byte[] passwordWithPepperBytes = passwordWithPepper.getBytes(StandardCharsets.UTF_8);

            // 3. Combine (password + pepper) with the extracted salt
            byte[] finalValueToHash = new byte[passwordWithPepperBytes.length + salt.length];
            System.arraycopy(passwordWithPepperBytes, 0, finalValueToHash, 0, passwordWithPepperBytes.length);
            System.arraycopy(salt, 0, finalValueToHash, passwordWithPepperBytes.length, salt.length);

            // 4. Hash the (rawPassword + pepper) + salt combination
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] currentHashedBytes = md.digest(finalValueToHash);

            // 5. Compare the newly generated hash with the original hash from storage
            boolean match = MessageDigest.isEqual(currentHashedBytes, originalHashedBytes);
            return match;

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hashing algorithm not found: " + HASH_ALGORITHM, e);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns true if the encoded password should be encoded again for better security.
     * For this simple example, we don't implement an upgrade strategy.
     */
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }

   
}
