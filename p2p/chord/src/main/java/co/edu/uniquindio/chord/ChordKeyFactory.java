package co.edu.uniquindio.chord;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;

import java.math.BigInteger;

public class ChordKeyFactory implements KeyFactory {
    private final HashingGenerator hashingGenerator;
    private int keyLength = 160;

    public ChordKeyFactory(HashingGenerator hashingGenerator, int keyLength) {
        this.hashingGenerator = hashingGenerator;
        this.keyLength = keyLength;
    }

    @Override
    public Key newKey(String value) {
        return new ChordKey(value, hashingGenerator.generateHashing(value, keyLength));
    }

    @Override
    public Key newKey(BigInteger hashing) {
        return new ChordKey(hashing);
    }

    @Override
    public int getKeyLength() {
        return keyLength;
    }

    @Override
    public void updateKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }
}
