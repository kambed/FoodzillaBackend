package pl.better.foodzillabackend.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.*;
import java.util.Set;

@Converter
public class SetToBlobConverter<T> implements AttributeConverter<Set<T>, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(Set set) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(set);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error serializing Set to byte[]", e);
        }
    }

    @Override
    public Set<T> convertToEntityAttribute(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Set<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error deserializing byte[] to Set", e);
        }
    }
}
