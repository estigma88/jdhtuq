package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.message.Message;

public interface Service {
    Message send(Message message);
}
