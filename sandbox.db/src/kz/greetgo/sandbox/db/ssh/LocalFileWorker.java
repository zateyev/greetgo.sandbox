package kz.greetgo.sandbox.db.ssh;

import com.jcraft.jsch.SftpException;
import kz.greetgo.util.RND;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
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
  public List<String> getAllFileNames(String ext) {
    File folder = new File(homePath);
    return Arrays.stream(folder.listFiles()).map(File::getName).collect(Collectors.toList());
  }

  private List<String> renameFiles(String ext) throws IOException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date nowDate = new Date();

    List<String> ret = new ArrayList<>();
    List<String> fileNames = getAllFileNames(ext);
    String regexPattern = "^[a-zA-Z0-9-_]*" + ext + "$";
    Pattern p = Pattern.compile(regexPattern);
    String processId = RND.intStr(5);

    for (String fileName : fileNames) {
      if (p.matcher(fileName).matches()) {
        String newName = fileName + "." + processId + "_" + sdf.format(nowDate);
        renameFile(fileName, newName);
        ret.add(newName);
      }
    }

    return ret;
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
  public List<String> getFileNamesToMigrate(String ext) throws IOException, SftpException {
    List<String> fileNamesToMigrate = renameFiles(ext);
    fileNamesToMigrate.sort(String::compareTo);
    return fileNamesToMigrate;
  }

  @Override
  public void close() throws Exception {

  }
}
