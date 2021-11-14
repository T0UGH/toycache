package com.t0ugh.server;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileTest {

    @Test
    public void testFile() throws IOException {
        OutputStream outputStream = new FileOutputStream("D:\\tmp\\tcwlog\\test\\test.log");
        outputStream.write("Hello".getBytes(StandardCharsets.UTF_8));
        File file = new File("D:\\tmp\\tcwlog\\test\\test.log");
        System.out.println(file.length());
        outputStream.close();
    }

    @Test
    public void testFile2() throws IOException {
        OutputStream outputStream = new FileOutputStream("D:\\tmp\\tcwlog\\test\\test.log");
        outputStream.write("Hello".getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        File file = new File("D:\\tmp\\tcwlog\\test\\test.log");
        file.delete();
        OutputStream outputStream1 = new FileOutputStream("D:\\tmp\\tcwlog\\test\\test.log");
        outputStream1.write("Hello".getBytes(StandardCharsets.UTF_8));
        outputStream1.close();
    }
}
