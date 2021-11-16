package com.t0ugh.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

    @Test
    public void testDeepCopy() throws Exception {
        Map<String, String> ori = Maps.newHashMap();
        ori.put("Hello", "World");
        Map<String, String> newM = Maps.newHashMap(ori);
        ori.put("Hello", "Hello");
        System.out.println();
    }

    @Test
    public void testDeepCopy2() throws Exception {
        List<String> ori = Lists.newArrayList();
        ori.add("Hello");
        List<String> newM = Lists.newArrayList(ori);
        ori.set(0, "Hi");
        System.out.println();
    }
}
