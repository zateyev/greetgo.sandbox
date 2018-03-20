package kz.greetgo.sandbox.db.ssh;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileWorker implements InputFileWorker {
  public String homePath;

  public LocalFileWorker(String homePath) {
    this.homePath = homePath;
  }

  @Override
  public InputStream downloadFile(String fileName) throws FileNotFoundException {
    return new FileInputStream(homePath + fileName);
  }

  @Override
  public void upload(File file) {

  }

  @Override
  public List<String> getFileNames(String ext) {
    File folder = new File(homePath);
    return Arrays.stream(folder.listFiles()).map(File::getName).collect(Collectors.toList());
  }

  @Override
  public void renameFile(String oldName, String newName) throws IOException {
    File file = new File(homePath + oldName);
    File fileWithNewName = new File(homePath + newName);
    if (fileWithNewName.exists())
      throw new java.io.IOException("file exists");
    boolean success = file.renameTo(fileWithNewName);
    if (!success) {
      throw new RuntimeException("File was not successfully renamed");
    }
  }

  @Override
  public void close() throws Exception {

  }
}
