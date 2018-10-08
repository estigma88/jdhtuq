package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static co.edu.uniquindio.dhash.resource.checksum.ChecksumInputStreamCalculator.CHECKSUM_ALGORITHM;

@Builder
@Data
public class LocalFileResource {
    @NonNull
    private final Resource resource;
    @NonNull
    private final String path;
    @NonNull
    private final Integer bufferSize;

    public File persist(ProgressStatus progressStatus) throws StorageException {
        Objects.requireNonNull(progressStatus);

        Path pathDestination = Paths.get(path).resolve(resource.getId());

        try (OutputStream destination = getDestination(pathDestination);
             InputStream source = resource.getInputStream()) {

            MessageDigest digest = getMessageDigestInstance();

            int count;
            long sent = 0L;
            byte[] buffer = new byte[bufferSize];

            progressStatus.status("resource-persist", sent, resource.getSize());
            progressStatus.status("digest-persist", sent, resource.getSize());

            while ((count = source.read(buffer)) > 0) {
                sent += count;

                destination.write(buffer, 0, count);

                progressStatus.status("resource-persist", sent, resource.getSize());

                digest.update(buffer, 0, count);

                progressStatus.status("digest-persist", sent, resource.getSize());
            }

            String checksum = getChecksum(digest);

            if (!checksum.equals(resource.getCheckSum())) {
                Files.delete(pathDestination);

                throw new StorageException("Checksum is not valid, " + checksum + " != " + resource.getCheckSum());
            }

            return pathDestination.toFile();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new StorageException("Problem persisting file", e);
        }
    }

    String getChecksum(MessageDigest digest) {
        return DatatypeConverter.printHexBinary(digest.digest());
    }

    MessageDigest getMessageDigestInstance() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(CHECKSUM_ALGORITHM);
    }

    FileOutputStream getDestination(Path pathDestination) throws FileNotFoundException {
        return new FileOutputStream(pathDestination.toFile());
    }
}
