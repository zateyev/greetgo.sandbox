package kz.greetgo.sandbox.db.ssh;

import com.jcraft.jsch.*;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class SshConnection implements AutoCloseable {
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

  public void connect() throws JSchException {
    session.connect();
    channelSftp = (ChannelSftp) session.openChannel("sftp");
    channelSftp.connect();
  }

  public InputStream download(String fileName) throws SftpException {
    return channelSftp.get(homePath + fileName);
  }

  public static void main(String args[]) {
    try (SshConnection sshConnection = new SshConnection("/home/zateyev/git/greetgo.sandbox/build/files_to_send/")) {
      String user = "zateyev";
      String password = "111";
      String host = "192.168.11.166";
      int port = 22;
      sshConnection.createSshConnection(user, password, host, port);

      FileOutputStream outError = new FileOutputStream(sshConnection.homePath + "test.txt");
      outError.write("Error: AAA".getBytes());
      outError.write("Error: BBB".getBytes());
      outError.write("Error: CCC".getBytes());
      sshConnection.upload(outError);
      outError.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void upload(FileOutputStream outputStream) {
    channelSftp.setOutputStream(outputStream);
//    channelSftp
  }

  public List<String> getFileNames(String ext) throws SftpException {
    Vector<ChannelSftp.LsEntry> list = channelSftp.ls(homePath + "*" + ext);
    return list.stream().map(ChannelSftp.LsEntry::getFilename).collect(Collectors.toList());
  }

  public void renameFile(String oldName, String newName) throws SftpException {
    channelSftp.rename(homePath + oldName, homePath + newName);
  }

  @Override
  public void close() throws Exception {
    session.disconnect();
    channelSftp.disconnect();
  }
}