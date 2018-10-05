package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.*;
import java.util.Objects;

@Builder
@Data
public class LocalFileResource {
    @NonNull
    private final Resource resource;
    @NonNull
    private final String path;
    @NonNull
    private final Integer sizeBuffer;

    public boolean persist(ProgressStatus progressStatus) throws IOException {
        Objects.requireNonNull(progressStatus);

        OutputStream destination = new FileOutputStream(path + "/" + resource.getId());
        InputStream source = resource.getInputStream();

        int count;
        long sent = 0L;
        byte[] buffer = new byte[sizeBuffer];

        progressStatus.status("resource-persist", sent, resource.getSize());

        while ((count = source.read(buffer)) > 0) {
            destination.write(buffer, 0, count);

            sent += count;

            progressStatus.status("resource-persist", sent, resource.getSize());
        }

        return true;
    }
}
