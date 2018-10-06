package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Builder
@Data
public class LocalFileResource {
    private static final String CHECKSUM_ALGORITHM = "MD5";

    @NonNull
    private final Resource resource;
    @NonNull
    private final String path;
    @NonNull
    private final Integer sizeBuffer;

    public File persist(ProgressStatus progressStatus) throws StorageException {
        Objects.requireNonNull(progressStatus);

        File fileDestination = new File(path + "/" + resource.getId());

        try (OutputStream destination = new FileOutputStream(fileDestination);
             InputStream source = resource.getInputStream()) {

            MessageDigest digest = MessageDigest.getInstance(CHECKSUM_ALGORITHM);

            int count;
            long sent = 0L;
            byte[] buffer = new byte[sizeBuffer];

            progressStatus.status("resource-persist", sent, resource.getSize());
            progressStatus.status("digest-persist", sent, resource.getSize());

            while ((count = source.read(buffer)) > 0) {
                destination.write(buffer, 0, count);

                progressStatus.status("resource-persist", sent, resource.getSize());

                digest.update(buffer, 0, count);

                progressStatus.status("digest-persist", sent, resource.getSize());

                sent += count;
            }

            String checksum = DatatypeConverter.printHexBinary(digest.digest());

            if (!checksum.equals(resource.getChecksum())) {
                throw new StorageException("Checksum is not valid, " + checksum + " != " + resource.getChecksum());
            }

            return fileDestination;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new StorageException("Problem persisting file", e);
        }
    }
}
