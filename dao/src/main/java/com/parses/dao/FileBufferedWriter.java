package com.parses.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileBufferedWriter {
    private final String filePath;

    public FileBufferedWriter(String filePath) {
        this.filePath = filePath;
    }

    public synchronized void write(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(content + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
