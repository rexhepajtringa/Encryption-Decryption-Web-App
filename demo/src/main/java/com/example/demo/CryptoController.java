package com.example.demo;

import com.example.demo.VerifyTextRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.security.*;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private KeystoreUtil keystoreUtil;

    @PostMapping("/create-keystore")
    public ResponseEntity<String> createKeystore(@RequestBody KeystoreRequest request) {
        try {
            char[] passwordArray = request.getPassword().toCharArray();
            String keystoreName = request.getName();
            if (keystoreName == null || keystoreName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Keystore name must be provided");
            }
            cryptoService.createKeystore(passwordArray, keystoreName);
            return ResponseEntity.ok("Keystore '" + keystoreName + "' created successfully.");
        } catch (KeyStoreException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + e.getMessage());
        }
    }


    @PostMapping("/login-keystore")
    public ResponseEntity<Boolean> loginKeystore(@RequestBody KeystoreRequest request) {
        try {
            boolean exists = keystoreUtil.keystoreExists(request.getPassword().toCharArray(), request.getName());
            if (exists) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/generate/aes")
    public ResponseEntity<String> generateAESKey(@RequestBody GenerateKeyRequest request) {
        try {
            SecretKey secretKey = cryptoService.generateAESKey(request.getKeySize(), request.getPassword().toCharArray(), request.getRandomAlgorithm(),
                    request.getSeed(), request.getKeystoreName());
            String aesAlias = "aes_" + request.getAlias();
            cryptoService.storeAESKey(aesAlias, secretKey, request.getPassword().toCharArray(), request.getKeystoreName());
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating/storing key: " + e.getMessage());
        }
    }

    @GetMapping("/load/aes")
    public ResponseEntity<String> loadAESKey(@RequestParam String alias, @RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            SecretKey secretKey = cryptoService.loadAESKey(alias, passwordArray, keystoreName);
            if (secretKey != null) {
                return ResponseEntity.ok(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            } else {
                return ResponseEntity.badRequest().body("Secret key not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading key: " + e.getMessage());
        }
    }

    @PostMapping("/encrypt/aes")
    public ResponseEntity<String> encryptAES(@RequestBody EncryptRequest request) {
        try {
            char[] passwordArray = request.getPassword().toCharArray();
            SecretKey secretKey = cryptoService.loadAESKey(request.getAlias(), passwordArray, request.getKeystoreName());
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            byte[] encryptedData = cryptoService.encryptAES(request.getPlainText(), secretKey, iv, request.getKeystoreName());
            String ivAndCipherText = Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedData);
            return ResponseEntity.ok(ivAndCipherText);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error encrypting data: " + e.getMessage());
        }
    }

    @PostMapping("/decrypt/aes")
    public ResponseEntity<String> decryptAES(@RequestBody DecryptRequest request) {
        try {
            char[] passwordArray = request.getPassword().toCharArray();
            SecretKey secretKey = cryptoService.loadAESKey(request.getAlias(), passwordArray, request.getKeystoreName());
            String[] parts = request.getCipherText().split(":");
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cipherText = Base64.getDecoder().decode(parts[1]);
            String decryptedData = cryptoService.decryptAES(cipherText, secretKey, iv, request.getKeystoreName());
            return ResponseEntity.ok(decryptedData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error decrypting data: " + e.getMessage());
        }
    }

    @GetMapping("/aliases")
    public ResponseEntity<List<String>> getAliases(@RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            List<String> aliases = cryptoService.getAliases(passwordArray, keystoreName);
            return ResponseEntity.ok(aliases);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/generate/rsa")
    public ResponseEntity<String> generateRSAKeyPair(@RequestBody GenerateKeyRequest request) {
        try {
            KeyPair keyPair = cryptoService.generateRSAKeyPair(request.getKeySize(), request.getPassword().toCharArray(), request.getRandomAlgorithm(),
                    request.getSeed(), request.getKeystoreName());
            String rsaAlias = "rsa_" + request.getAlias();
            cryptoService.storeRSAKeyPair(rsaAlias, keyPair, request.getPassword().toCharArray(), request.getKeystoreName());
            return ResponseEntity.ok("RSA key pair generated and stored successfully in " + request.getKeystoreName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating/storing RSA key pair: " + e.getMessage());
        }
    }

    @PostMapping("/encrypt/rsa")
    public ResponseEntity<String> encryptRSA(@RequestBody EncryptRequest request) {
        try {
            byte[] encryptedData = cryptoService.encryptRSAWithPublicKeyFromFile(request.getPlainText(), request.getAlias());
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(encryptedData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error encrypting data: " + e.getMessage());
        }
    }

    @PostMapping("/decrypt/rsa")
    public ResponseEntity<String> decryptRSA(@RequestBody DecryptRequest request) {
        try {
            char[] passwordArray = request.getPassword().toCharArray();
            PrivateKey privateKey = cryptoService.loadPrivateKey(request.getAlias(), passwordArray, request.getKeystoreName());
            byte[] decodedData = Base64.getDecoder().decode(request.getCipherText());
            String decryptedData = cryptoService.decryptRSA(decodedData, privateKey);
            return ResponseEntity.ok(decryptedData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error decrypting data: " + e.getMessage());
        }
    }

    @PostMapping("/generate/dsa")
    public ResponseEntity<String> generateDSAKeyPair(@RequestBody GenerateKeyRequest request) {
        try {
            KeyPair keyPair = cryptoService.generateDSAKeyPair(request.getKeySize(), request.getPassword().toCharArray(), request.getRandomAlgorithm(),
                    request.getSeed(), request.getKeystoreName());
            String dsaAlias = "dsa_" + request.getAlias();
            cryptoService.storeDSAKeyPair(dsaAlias, keyPair, request.getPassword().toCharArray(), request.getKeystoreName());
            return ResponseEntity.ok("DSA key pair generated and stored successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating/storing DSA key pair: " + e.getMessage());
        }
    }

    @PostMapping("/sign-text")
    public ResponseEntity<String> signText(@RequestBody EncryptRequest request) {
        try {
            char[] passwordArray = request.getPassword().toCharArray();
            PrivateKey privateKey = cryptoService.loadPrivateKey(request.getAlias(), passwordArray, request.getKeystoreName());
            byte[] data = request.getPlainText().getBytes();
            byte[] signature = cryptoService.signData(data, privateKey);
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(signature));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error signing text: " + e.getMessage());
        }
    }

    @PostMapping("/verify-text")
    public ResponseEntity<?> verifyTextSignature(@RequestBody VerifyTextRequest request) {
        try {
            byte[] data = request.getText().getBytes();
            byte[] signatureBytes = Base64.getDecoder().decode(request.getSignature());
            boolean isValid = cryptoService.verifyDSASignatureFromFile(data, signatureBytes, request.getAlias());
            return ResponseEntity.ok(isValid ? true : false);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying signature: " + e.getMessage());
        }
    }

    @GetMapping("/rsa/public")
    public ResponseEntity<String> getRSAPublicKeyNoPassword(@RequestParam String alias) {
        try {
            PublicKey publicKey = keystoreUtil.loadPublicKeyFromPEM(alias, "RSA");
            if (publicKey != null) {
                return ResponseEntity.ok(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            } else {
                return ResponseEntity.badRequest().body("Public key not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading public key: " + e.getMessage());
        }
    }

    @GetMapping("/dsa/public")
    public ResponseEntity<String> getDSAPublicKeyNoPassword(@RequestParam String alias) {
        try {
            PublicKey publicKey = keystoreUtil.loadPublicKeyFromPEM(alias, "DSA");
            if (publicKey != null) {
                return ResponseEntity.ok(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            } else {
                return ResponseEntity.badRequest().body("Public key not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading public key: " + e.getMessage());
        }
    }

    @GetMapping("/rsa/private")
    public ResponseEntity<String> getRSAPrivateKey(@RequestParam String alias, @RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            PrivateKey privateKey = cryptoService.loadPrivateKey(alias, passwordArray, keystoreName);
            if (privateKey != null) {
                return ResponseEntity.ok(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            } else {
                return ResponseEntity.badRequest().body("Private key not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading private key: " + e.getMessage());
        }
    }

    @GetMapping("/dsa/private")
    public ResponseEntity<String> getDSAPrivateKey(@RequestParam String alias, @RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            PrivateKey privateKey = cryptoService.loadPrivateKey(alias, passwordArray, keystoreName);
            if (privateKey != null) {
                return ResponseEntity.ok(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            } else {
                return ResponseEntity.badRequest().body("Private key not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading private key: " + e.getMessage());
        }
    }

    @PostMapping("/filter-aliases")
    public ResponseEntity<List<String>> filterAliases(@RequestBody FilterAliasesRequest request) {
        try {
            char[] passwordArray = request.getPassword().toCharArray();
            List<String> filteredAliases = cryptoService.filterAliases(passwordArray, request.getFilter(), request.getKeystoreName());
            return ResponseEntity.ok(filteredAliases);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

@DeleteMapping("/delete/aes")
    public ResponseEntity<String> deleteAESKey(@RequestParam String alias, @RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            cryptoService.deleteAESKey(alias, passwordArray, keystoreName);
            return ResponseEntity.ok("AES key deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting AES key: " + e.getMessage());
        }
    }

    @PostMapping("/delete/rsa")
    public ResponseEntity<String> deleteRSAKeyPair(@RequestParam String alias, @RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            cryptoService.deleteRSAKeyPair(alias, passwordArray, keystoreName);
            return ResponseEntity.ok("RSA key pair and PEM files deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting RSA key pair: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/dsa")
    public ResponseEntity<String> deleteDSAKeyPair(@RequestParam String alias, @RequestParam String password, @RequestParam String keystoreName) {
        try {
            char[] passwordArray = password.toCharArray();
            cryptoService.deleteDSAKeyPair(alias, passwordArray, keystoreName);
            return ResponseEntity.ok("DSA key pair and PEM files deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting DSA key pair: " + e.getMessage());
        }
    }

    @GetMapping("/public-keys")
    public ResponseEntity<List<String>> getAllPublicKeyNames() {
        try {
            List<String> publicKeyNames = keystoreUtil.getAllPublicKeyNames();
            if (publicKeyNames.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(publicKeyNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
