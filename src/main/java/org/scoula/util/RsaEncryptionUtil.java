package org.scoula.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaEncryptionUtil {

    public String encryptPassword(String publicKeyContent, String plainPassword) throws Exception {
        String cleanedKey = publicKeyContent
                .replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKeyBytes;
        try {
            decodedKeyBytes = Base64.getDecoder().decode(cleanedKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("공개키(Base64)가 올바르지 않습니다. 디코딩 실패", e);
        }

        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKeyBytes);
        PublicKey publicKey = kf.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainPassword.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

}
