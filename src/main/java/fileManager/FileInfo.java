package fileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    public static final String UpToken = "[..]";
    private String fileName;
    private long length;

    //нужен элемент, который будет отображаться всегда верхним в списке файлов
    public FileInfo(String fileName, long length) {
        this.fileName = fileName;
        this.length = length;
    }

    public FileInfo(Path path) {
        this.fileName = path.getFileName().toString();
        if (Files.isDirectory(path)) {
            this.length = -1;
        } else {
            try {
                this.length = Files.size(path);
            } catch (IOException e) {
                throw new RuntimeException("Something wrong with file: " + path.toAbsolutePath().toString());
            }
        }
    }

    //возвращает true, если объет FileInfo является папкой
    public boolean isDirectory() {
        return length == -1L;
    }

    //возвращает true, если объет FileInfo является папкой
    public boolean isUpElement() {
        return length == -2L;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getLength() {
        return length;
    }
    public void setLength(long length) {
        this.length = length;
    }
}
