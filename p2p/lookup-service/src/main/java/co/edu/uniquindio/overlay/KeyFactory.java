package co.edu.uniquindio.overlay;

import java.math.BigInteger;

public interface KeyFactory {
    Key newKey(String value);

    Key newKey(BigInteger hashing);

    int getKeyLength();

    void updateKeyLength(int keyLength);
}
