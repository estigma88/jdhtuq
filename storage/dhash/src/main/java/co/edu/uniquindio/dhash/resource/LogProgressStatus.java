package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.ProgressStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class LogProgressStatus implements ProgressStatus{
    @NonNull
    private final String id;

    @Override
    public void status(String name, Long current, Long size) {
        log.debug("{} - {} - {} - {}", id, name, current, size);
    }
}
