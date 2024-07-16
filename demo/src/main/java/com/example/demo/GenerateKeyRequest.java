package com.example.demo;

public class GenerateKeyRequest {
    private int keySize;
    private String alias;
    private String password;
    private String randomAlgorithm;
    private Long seed;
    private String keystoreName;

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRandomAlgorithm() {
        return randomAlgorithm;
    }

    public void setRandomAlgorithm(String randomAlgorithm) {
        this.randomAlgorithm = randomAlgorithm;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getKeystoreName() {
        return keystoreName;
    }

    public void setKeystoreName(String keystoreName) {
        this.keystoreName = keystoreName;
    }

}
