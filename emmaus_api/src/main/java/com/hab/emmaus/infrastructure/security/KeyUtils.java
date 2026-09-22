package com.hab.emmaus.infrastructure.security;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {
    private KeyUtils() {}

    public static PrivateKey loadPrivateKey(final String pemPath) throws Exception {
        // Updated to read from file system
        final String key = readKeyFromFile(pemPath).replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        final byte[] decoded = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    public static PublicKey loadPublicKey(final String pemPath) throws Exception {
        // Updated to read from file system
        final String key = readKeyFromFile(pemPath).replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        final byte[] decoded = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    // New helper method using java.nio.file
    private static String readKeyFromFile(final String path) throws Exception {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Key not found at file system path: " + path, e);
        }
    }
}
