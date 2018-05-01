package co.edu.uniquindio.utils.communication.web.restful;

import co.edu.uniquindio.utils.communication.message.Message;
import org.springframework.web.bind.annotation.*;

//@RestController
public class RestfulWebProcessor {
    private final RestfulWebCommunicationManagerFactory restfulWebCommunicationManagerFactory;

    public RestfulWebProcessor(RestfulWebCommunicationManagerFactory restfulWebCommunicationManagerFactory) {
        this.restfulWebCommunicationManagerFactory = restfulWebCommunicationManagerFactory;
    }

    //communication/{communicationName}/messages/
    //@RequestMapping(method = RequestMethod.POST, path = "${communication.web.restful.request-path}")
    public Message process(@PathVariable String communicationName, @RequestBody Message request) {
        return restfulWebCommunicationManagerFactory.getMessageProcessor(communicationName).process(request);
    }
}
