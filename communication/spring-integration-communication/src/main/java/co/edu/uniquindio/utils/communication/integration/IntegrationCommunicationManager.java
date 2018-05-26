package co.edu.uniquindio.utils.communication.integration;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.integration.sender.MessageSender;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

public class IntegrationCommunicationManager implements CommunicationManager {
    private final Observable<Message> observable;
    private final MessageProcessorWrapper messageProcessorWrapper;
    private final MessageSender directSender;
    private final MessageSender multicastSender;
    private final MessageResponseProcessor messageResponseProcessor;
    private final boolean multicastSenderActive;

    IntegrationCommunicationManager(Observable<Message> observable, MessageProcessorWrapper messageProcessorWrapper, MessageSender directSender, MessageSender multicastSender, MessageResponseProcessor messageResponseProcessor, boolean multicastSenderActive) {
        this.observable = observable;
        this.messageProcessorWrapper = messageProcessorWrapper;
        this.directSender = directSender;
        this.multicastSender = multicastSender;
        this.messageResponseProcessor = messageResponseProcessor;
        this.multicastSenderActive = multicastSenderActive;
    }

    @Override
    public void init() {
        this.directSender.start();

        if (this.multicastSenderActive) {
            this.multicastSender.start();
        }
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn) {
        return sendMessageUnicast(message, typeReturn, null);
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn, String paramNameResult) {
        observable.notifyMessage(message);

        Message responseMessage = directSender.send(message);

        return messageResponseProcessor.process(responseMessage, typeReturn, paramNameResult);
    }

    @Override
    public void sendMessageUnicast(Message message) {
        sendMessageUnicast(message, Message.class);
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn) {
        return sendMessageMultiCast(message, typeReturn, null);
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn, String paramNameResult) {
        if (multicastSenderActive) {
            Message responseMessage = multicastSender.send(message);

            return messageResponseProcessor.process(responseMessage, typeReturn, paramNameResult);
        } else {
            throw new IllegalStateException("Multicast server is not active or you must call 'init'");
        }
    }

    @Override
    public void sendMessageMultiCast(Message message) {
        sendMessageMultiCast(message, Message.class);
    }

    @Override
    public void stopAll() {
        this.directSender.stop();

        if (this.multicastSenderActive) {
            this.multicastSender.stop();
        }
    }

    @Override
    public void addObserver(Observer<Message> observer) {
        this.observable.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer<Message> observer) {
        this.observable.removeObserver(observer);
    }

    @Override
    public void removeObserver(String name) {
        this.observable.removeObserver(name);
    }

    @Override
    public void addMessageProcessor(String name, MessageProcessor messageProcessor) {
        this.messageProcessorWrapper.updateMessageProcessor(messageProcessor);
    }

    @Override
    public void removeMessageProcessor(String name) {
        this.messageProcessorWrapper.updateMessageProcessor(null);
    }
}
