package org.example.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializeUtil {

    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    public static <T> byte[] writeToBytes(T t) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Output output = new Output(outputStream)) {
            kryos.get().writeClassAndObject(output, t);
            output.flush();
            return outputStream.toByteArray();
        }
    }

    public static <T> T readFromBytes(byte[] bytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(inputStream)) {
            return (T) kryos.get().readClassAndObject(input);
        }
    }

}
