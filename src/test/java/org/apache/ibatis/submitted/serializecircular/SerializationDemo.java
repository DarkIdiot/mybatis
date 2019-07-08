package org.apache.ibatis.submitted.serializecircular;

import java.io.*;

class SerializationSuperClass implements Serializable {
    private String superField;
}

class SerializationComponentClass implements Serializable {
    private String componentField;
}

public class SerializationDemo extends SerializationSuperClass implements Serializable {
    private SerializationComponentClass component;
    private String stringField;
    private int intField;

    public SerializationDemo(String s, int i) {
        this.stringField = s;
        this.intField = i;
    }

    public static void main(String[] args) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream("text.bin");
        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
        out.writeObject(new SerializationDemo("darkidiot", 1234567890));
    }
}
