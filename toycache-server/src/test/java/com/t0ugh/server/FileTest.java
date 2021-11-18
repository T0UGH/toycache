package com.t0ugh.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.t0ugh.server.storage.MemoryComparableString;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

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

    @Test
    public void testComparableString() throws Exception {
        // todo: 如果string一样，不会用前一个的score覆盖后一个的score
        MemoryComparableString s1 = new MemoryComparableString("Hello", 1);
        MemoryComparableString s2 = new MemoryComparableString("Hello", 3);
        MemoryComparableString s3 = new MemoryComparableString("Hi", 5);
        System.out.println(s1.compareTo(s2));
        SortedSet<MemoryComparableString> sortedSet = Sets.newTreeSet();
        sortedSet.add(s1);
        sortedSet.add(s2);
        sortedSet.add(s3);
        sortedSet.remove(s2);
        sortedSet.add(s2);
        System.out.println(sortedSet);
    }
}
