package kz.greetgo.sandbox.db.ssh;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class SshConnection implements InputFileWorker {
  private Session session;
  private ChannelSftp channelSftp;
  private String homePath;

  public SshConnection(String homePath) {
    this.homePath = homePath;
  }

  public void createSshConnection(String user, String psw, String host, int port) throws JSchException {
    JSch jsch = new JSch();

    session = jsch.getSession(user, host, port);
    session.setPassword(psw);
    session.setConfig("StrictHostKeyChecking", "no");

    connect();

  }

  private void connect() throws JSchException {
    session.connect();
    channelSftp = (ChannelSftp) session.openChannel("sftp");
    channelSftp.connect();
  }

  @Override
  public InputStream downloadFile(String fileName) throws SftpException {
    return channelSftp.get(homePath + fileName);
  }

  @Override
  public void upload(File file) throws FileNotFoundException, SftpException {
    channelSftp.cd(homePath);
    channelSftp.put(new FileInputStream(file), file.getName());
  }

  @Override
  public List<String> getFileNames(String ext) throws SftpException {
    Vector<ChannelSftp.LsEntry> list = channelSftp.ls(homePath + "*" + ext);
    return list.stream().map(ChannelSftp.LsEntry::getFilename).collect(Collectors.toList());
  }

  @Override
  public void renameFile(String oldName, String newName) throws SftpException {
    channelSftp.rename(homePath + oldName, homePath + newName);
  }

  @Override
  public void close() throws Exception {
    session.disconnect();
    channelSftp.disconnect();
  }
}