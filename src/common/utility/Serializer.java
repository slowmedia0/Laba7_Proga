package common.utility;

import java.io.*;


public class Serializer {

    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null || bytes.length == 0) {
            throw new IOException("Пустой массив байт для десериализации");
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        }
    }
}