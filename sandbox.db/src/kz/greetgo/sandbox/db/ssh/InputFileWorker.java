package kz.greetgo.sandbox.db.ssh;

import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface InputFileWorker extends AutoCloseable{
  InputStream downloadFile(String fileName) throws FileNotFoundException, SftpException;
  void upload(File file) throws FileNotFoundException, SftpException;
  List<String> getFileNames(String ext) throws SftpException;
  void renameFile(String oldName, String newName) throws IOException, SftpException;
}
