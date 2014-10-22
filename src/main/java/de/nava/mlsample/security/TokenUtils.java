package de.nava.mlsample.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Philip W. Sorst (philip@sorst.net)
 * @author Josh Long (josh@joshlong.com)
 * @author Niko Schmuck (niko@nava.de)
 */
public final class TokenUtils {

    public static final String MAGIC_KEY = "obfuscate";
    public static final long EXPIRES_IN_MS = 60 * 60 * 1000L;

    public static String createToken(UserDetails userDetails) {
        long expires = System.currentTimeMillis() + EXPIRES_IN_MS;
        return userDetails.getUsername() + ':' + expires + ':' + computeSignature(userDetails, expires);
    }

    public static String computeSignature(UserDetails userDetails, long expires) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        String signature = userDetails.getUsername() + ':' + expires + ':' + ':' + MAGIC_KEY;
        return new String(Hex.encode(digest.digest(signature.getBytes())));
    }

    public static String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(":");
        return parts[0];
    }

    public static boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
        String signatureToMatch = computeSignature(userDetails, expires);
        return expires >= System.currentTimeMillis() && signature.equals(signatureToMatch);
    }
}