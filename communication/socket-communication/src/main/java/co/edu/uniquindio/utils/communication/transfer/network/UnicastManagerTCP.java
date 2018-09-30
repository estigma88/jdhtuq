/*
 *  Communication project implement communication point to point and multicast
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.ProgressStatusTransfer;
import co.edu.uniquindio.utils.communication.transfer.StreamCommunicator;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import static co.edu.uniquindio.utils.communication.transfer.response.ConnectionMessageProcessorGateway.HANDLE_STREAMS;

/**
 * The <code>UnicastManagerTCP</code> class implemented transfer message based
 * in TCP
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
@Slf4j
public class UnicastManagerTCP implements StreamCommunicator {
    public enum UnicastManagerTCPProperties {
        TIMEOUT_TCP_CONNECTION, PORT_TCP, SIZE_TCP_BUFFER
    }

    private ServerSocket serverSocket;
    private int portTcp;
    private int timeoutTcpConnection;
    private int sizeBuffer;
    private final MessageSerialization messageSerialization;

    UnicastManagerTCP(MessageSerialization messageSerialization) {
        this.messageSerialization = messageSerialization;
    }

    @Override
    public Message receive() {
        try (Socket socket = listen()) {
            return readMessage(socket.getInputStream());
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error reading socket", e);
        }

        return null;
    }

    @Override
    public MessageStream receive(Message message, ProgressStatusTransfer progressStatusTransfer) {
        try {
            Socket socket = new Socket();

            message = Message.with(message)
                    .param(HANDLE_STREAMS, String.valueOf(true))
                    .build();

            progressStatusTransfer.status("message-starter", 0L, 1L);

            send(message, socket);

            progressStatusTransfer.status("message-starter", 1L, 1L);
            progressStatusTransfer.status("message-ack", 0L, 1L);

            Message messageResponse = readMessage(socket.getInputStream());

            progressStatusTransfer.status("message-ack", 1L, 1L);

            return MessageStream.builder()
                    .message(messageResponse)
                    .inputStream(socket.getInputStream())
                    .build();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error writing socket " + message.getAddress(), e);
            return null;
        }
    }

    @Override
    public Socket listen() throws IOException {
        return serverSocket.accept();
    }

    @Override
    public void send(Message message) {
        try (Socket socket = new Socket()) {
            send(message, socket);
        } catch (IOException e) {
            log.error("Error writing socket " + message.getAddress(), e);
        }

    }

    @Override
    public void send(MessageStream messageStream, ProgressStatusTransfer progressStatusTransfer) {
        try (Socket socket = new Socket()) {
            Message message = Message.with(messageStream.getMessage())
                    .param(HANDLE_STREAMS, String.valueOf(true))
                    .build();

            progressStatusTransfer.status("message-starter", 0L, 1L);

            send(message, socket);

            progressStatusTransfer.status("message-starter", 1L, 1L);

            send(socket.getOutputStream(), messageStream.getInputStream(), messageStream.getSize(), progressStatusTransfer);
        } catch (IOException e) {
            log.error("Error writing socket", e);
        }
    }

    @Override
    public void send(MessageStream messageStream, OutputStream destination, ProgressStatusTransfer progressStatusTransfer) {
        progressStatusTransfer.status("message-starter", 0L, 1L);

        sendTo(messageStream.getMessage(), destination);

        progressStatusTransfer.status("message-starter", 1L, 1L);

        try {
            send(destination, messageStream.getInputStream(), messageStream.getSize(), progressStatusTransfer);
        } catch (IOException e) {
            log.error("Error writing socket", e);
        }
    }

    @Override
    public void start(Map<String, String> properties) {
        portTcp = Optional.ofNullable(properties.get(UnicastManagerTCPProperties.PORT_TCP.name().toLowerCase()))
                .map(Integer::parseInt)
                .orElseThrow(() -> new IllegalArgumentException("Property port_tcp not found"));

        timeoutTcpConnection = Optional.ofNullable(properties.get(UnicastManagerTCPProperties.TIMEOUT_TCP_CONNECTION.name().toLowerCase()))
                .map(Integer::parseInt)
                .orElseThrow(() -> new IllegalArgumentException("Property timeout_tcp_connection not found"));

        sizeBuffer = Optional.ofNullable(properties.get(UnicastManagerTCPProperties.SIZE_TCP_BUFFER.name().toLowerCase()))
                .map(Integer::parseInt)
                .orElseThrow(() -> new IllegalArgumentException("Property size_tcp_buffer not found"));

        try {
            this.serverSocket = new ServerSocket(portTcp);
        } catch (IOException e) {
            log.error("Error creating server socket", e);
            throw new IllegalStateException("Error creating server socket", e);
        }

    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("Error closing server socket", e);
            throw new IllegalStateException("Error closing server socket", e);
        }
    }

    private Message readMessage(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        String stringMessage = (String) objectInputStream.readObject();

        return messageSerialization.decode(stringMessage);
    }

    private void send(Message message, Socket socket) throws IOException {
        socket.connect(new InetSocketAddress(message.getAddress().getDestination(), portTcp), timeoutTcpConnection);

        sendTo(message, socket.getOutputStream());
    }

    private void send(OutputStream destination, InputStream source, Long size, ProgressStatusTransfer progressStatusTransfer) throws IOException {
        int count;
        long sent = 0L;
        byte[] buffer = new byte[sizeBuffer];

        progressStatusTransfer.status("stream-transfer", sent, size);

        while ((count = source.read(buffer)) > 0) {
            destination.write(buffer, 0, count);

            sent += count;

            progressStatusTransfer.status("stream-transfer", sent, size);
        }
        source.close();
        destination.close();
    }


    private void sendTo(Message message, OutputStream destination) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    destination);
            objectOutputStream.writeObject(messageSerialization.encode(message));
        } catch (IOException e) {
            log.error("Error writing socket " + message.getAddress(), e);
        }
    }
}
