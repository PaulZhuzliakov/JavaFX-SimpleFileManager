package fileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private String fileName;
    private long length;

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
