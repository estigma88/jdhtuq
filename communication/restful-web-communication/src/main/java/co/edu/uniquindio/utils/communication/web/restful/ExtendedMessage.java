package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.message.Message;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExtendedMessage {
    private final String replyChannelId;
    private final String errorChannelId;
    private final Message data;
}
