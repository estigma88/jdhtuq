package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.storage.resource.Resource;

import java.io.*;

public class ObjectSerializationHandler implements SerializationHandler {
    @Override
    public byte[] encode(Resource resource) {
        try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutput)) {

            objectOutput.writeObject(resource);

            return byteArrayOutput.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error meanwhile serialization", e);
        }
    }

    @Override
    public Resource decode(byte[] bytes) {
        try (ByteArrayInputStream byteArrayOutput = new ByteArrayInputStream(bytes);
             ObjectInputStream objectOutput = new ObjectInputStream(byteArrayOutput)) {

            return (Resource) objectOutput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Error meanwhile deserialization", e);
        }
    }
}
