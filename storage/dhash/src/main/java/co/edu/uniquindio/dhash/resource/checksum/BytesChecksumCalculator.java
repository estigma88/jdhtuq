package co.edu.uniquindio.dhash.resource.checksum;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.storage.resource.Resource;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BytesChecksumCalculator implements ChecksumCalculator {
    private String algorithm = "MD5";

    @Override
    public String calculate(Resource resource) {
        BytesResource bytesResource = (BytesResource) resource;

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] md5sum = digest.digest(bytesResource.getBytes());

            BigInteger bigInt = new BigInteger(1, md5sum);

            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Error with algorithm", e);
        }
    }
}
