package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CryptoService {

    @Autowired
    private KeystoreUtil keystoreUtil;

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public void createKeystore(char[] password, String keystoreName) throws Exception {
        keystoreUtil.createKeystore(password, keystoreName);
    }

    public SecretKey generateAESKey(int keySize, char[] password, String randomAlgorithm, Long seed, String keystoreName) throws Exception {
        SecureRandom secureRandom = getSecureRandom(randomAlgorithm, seed);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize, secureRandom);
        SecretKey secretKey = keyGen.generateKey();
        storeAESKey("aes_" + System.currentTimeMillis(), secretKey, password, keystoreName);
        return secretKey;
    }

    public void storeAESKey(String alias, SecretKey secretKey, char[] password, String keystoreName) throws Exception {
        keystoreUtil.storeSecretKey(alias, secretKey, password, keystoreName);
    }

    public SecretKey loadAESKey(String alias, char[] password, String keystoreName) throws Exception {
        return keystoreUtil.loadSecretKey(alias, password, keystoreName);
    }

    public KeyPair generateRSAKeyPair(int keySize, char[] password, String randomAlgorithm, Long seed, String keystoreName) throws Exception {
        SecureRandom secureRandom = getSecureRandom(randomAlgorithm, seed);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        storeRSAKeyPair("rsa_" + System.currentTimeMillis(), keyPair, password, keystoreName);
        return keyPair;
    }

    public void storeRSAKeyPair(String alias, KeyPair keyPair, char[] password, String keystoreName) throws Exception {
        keystoreUtil.storeRSAKeyPair(alias, keyPair, password, keystoreName);
    }

    public PrivateKey loadPrivateKey(String alias, char[] password, String keystoreName) throws Exception {
        return keystoreUtil.loadPrivateKey(alias, password, keystoreName);
    }

    public PublicKey loadPublicKey(String alias, char[] password, String keystoreName) throws Exception {
        return keystoreUtil.loadPublicKey(alias, password, keystoreName);
    }

    public List<String> getAliases(char[] password, String keystoreName) throws Exception {
        return keystoreUtil.getAliases(password, keystoreName);
    }

    public byte[] encryptRSA(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public String decryptRSA(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public byte[] encryptAES(String plainText, SecretKey secretKey, byte[] iv, String keystoreName) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public String decryptAES(byte[] cipherText, SecretKey secretKey, byte[] iv, String keystoreName) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    public SecureRandom getSecureRandom(String algorithm, Long seed) throws NoSuchAlgorithmException {
        SecureRandom random;
        if (algorithm != null && !algorithm.isEmpty()) {
            random = SecureRandom.getInstance(algorithm);
        } else {
            random = new SecureRandom();
        }
        if (seed != null) {
            random.setSeed(seed);
        }
        return random;
    }

    public KeyPair generateDSAKeyPair(int keySize, char[] password, String randomAlgorithm, Long seed, String keystoreName) throws Exception {
        SecureRandom secureRandom = getSecureRandom(randomAlgorithm, seed);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(keySize, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        storeDSAKeyPair("dsa_" + System.currentTimeMillis(), keyPair, password, keystoreName);
        return keyPair;
    }

    public void storeDSAKeyPair(String alias, KeyPair keyPair, char[] password, String keystoreName) throws Exception {
        keystoreUtil.storeDSAKeyPair(alias, keyPair, password, keystoreName);
    }

    public byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withDSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withDSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    public List<String> filterAliases(char[] password, String filter, String keystoreName) throws Exception {
        List<String> aliases = keystoreUtil.getAliases(password, keystoreName);
        return aliases.stream()
                .filter(alias -> alias.contains(filter))
                .collect(Collectors.toList());
    }

    public void deleteAESKey(String alias, char[] password, String keystoreName) throws Exception {
        keystoreUtil.deleteKey(alias, password, keystoreName);
    }

    public void deleteRSAKeyPair(String alias, char[] password, String keystoreName) throws Exception {
        keystoreUtil.deleteKey(alias, password, keystoreName);
        keystoreUtil.deletePEMFiles(alias, "rsa");
    }

    public void deleteDSAKeyPair(String alias, char[] password, String keystoreName) throws Exception {
        keystoreUtil.deleteKey(alias, password, keystoreName);
        keystoreUtil.deletePEMFiles(alias, "dsa");
    }

    public PublicKey loadPublicKeyNoPassword(String alias, String keyType) throws Exception {
        KeyStore keystore = keystoreUtil.loadKeystoreWithoutPassword();
        java.security.cert.Certificate cert = keystore.getCertificate(alias);
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }
    
    public byte[] encryptRSAWithPublicKeyFromFile(String plainText, String alias) throws Exception {
        PublicKey publicKey = keystoreUtil.loadPublicKeyFromPEM(alias, "RSA");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verifyDSASignatureFromFile(byte[] data, byte[] signatureBytes, String alias) throws Exception {
        PublicKey publicKey = keystoreUtil.loadPublicKeyFromPEM(alias, "DSA");
        Signature signature = Signature.getInstance("SHA256withDSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

}
