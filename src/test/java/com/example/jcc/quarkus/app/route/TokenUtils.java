//package com.example.jcc.quarkus.app.route;
//
//import io.smallrye.jwt.build.Jwt;
//import io.smallrye.jwt.build.JwtClaimsBuilder;
//import org.eclipse.microprofile.jwt.Claims;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.security.KeyFactory;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.util.Base64;
//import java.util.Map;
//
///**
// * Utilities for generating a JWT for testing
// */
//public class TokenUtils {
//
//    private TokenUtils() {
//        // no-op: utility class
//    }
//
//    /**
//     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
//     * test resource key, possibly with invalid fields.
//     *
//     * @param jsonResName - name of test resources file
//     * @param timeClaims - used to return the exp, iat, auth_time claims
//     * @return the JWT string
//     */
//    public static String generateTokenString(String jsonResName, Map<String, Long> timeClaims)
//            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
//        // Use the test private key associated with the test public key for a valid signature
//        PrivateKey pk = readPrivateKey("/privateKey.pem");
//        return generateTokenString(pk, "/privateKey.pem", jsonResName, timeClaims);
//    }
//
//    public static String generateTokenString(PrivateKey privateKey, String kid,
//                                             String jsonResName, Map<String, Long> timeClaims) {
//
//        JwtClaimsBuilder claims = Jwt.claims(jsonResName);
//        long currentTimeInSecs = currentTimeInSecs();
//        long exp = timeClaims != null && timeClaims.containsKey(Claims.exp.name())
//                ? timeClaims.get(Claims.exp.name()) : currentTimeInSecs + 300;
//
//        claims.issuedAt(currentTimeInSecs);
//        claims.claim(Claims.auth_time.name(), currentTimeInSecs);
//        claims.expiresAt(exp);
//
//        return claims.jws().signatureKeyId(kid).sign(privateKey);
//    }
//
//    /**
//     * Read a PEM encoded private key from the classpath
//     *
//     * @param pemResName - key file resource name
//     * @return PrivateKey
//     */
//    public static PrivateKey readPrivateKey(final String pemResName) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
//        try (InputStream contentIS = TokenUtils.class.getResourceAsStream(pemResName)) {
//            byte[] tmp = new byte[4096];
//            int length = contentIS.read(tmp);
//            return decodePrivateKey(new String(tmp, 0, length, StandardCharsets.UTF_8));
//        }
//    }
//
//    /**
//     * Decode a PEM encoded private key string to an RSA PrivateKey
//     *
//     * @param pemEncoded - PEM string for private key
//     * @return PrivateKey
//     */
//    public static PrivateKey decodePrivateKey(final String pemEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        byte[] encodedBytes = toEncodedBytes(pemEncoded);
//
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return kf.generatePrivate(keySpec);
//    }
//
//    private static byte[] toEncodedBytes(final String pemEncoded) {
//        final String normalizedPem = removeBeginEnd(pemEncoded);
//        return Base64.getDecoder().decode(normalizedPem);
//    }
//
//    private static String removeBeginEnd(String pem) {
//        pem = pem.replaceAll("-----BEGIN (.*)-----", "");
//        pem = pem.replaceAll("-----END (.*)----", "");
//        pem = pem.replaceAll("\r\n", "");
//        pem = pem.replaceAll("\n", "");
//        return pem.trim();
//    }
//
//    /**
//     * @return the current time in seconds since epoch
//     */
//    public static int currentTimeInSecs() {
//        long currentTimeMS = System.currentTimeMillis();
//        return (int) (currentTimeMS / 1000);
//    }
//
//}
