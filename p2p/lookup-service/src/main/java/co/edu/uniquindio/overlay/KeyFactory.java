package co.edu.uniquindio.overlay;

import java.math.BigInteger;

/**
 * Factory of keys
 */
public interface KeyFactory {
    /**
     * Create a new key
     *
     * @param value key value
     * @return new key
     */
    Key newKey(String value);

    /**
     * Create a new key with the hashing value
     *
     * @param hashing key hashing
     * @return new key
     */
    Key newKey(BigInteger hashing);

    /**
     * Key length
     *
     * @return key length
     */
    int getKeyLength();

    /**
     * Update the key length
     *
     * @param keyLength new key length
     */
    void updateKeyLength(int keyLength);
}
