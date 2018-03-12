package kz.greetgo.sandbox.db.ssh;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SshConnection implements AutoCloseable {
  public Session session;
  public ChannelSftp channelSftp;

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

  public InputStream download(String remoteFilePath, String fileName) throws SftpException {
    return channelSftp.get(remoteFilePath + fileName);
  }

  public static void main(String args[]) {
    try (SshConnection sshConnection = new SshConnection()) {
      String user = "john";
      String password = "mypassword";
      String host = "192.168.100.23";
      int port = 22;

      String remoteFilePath = "/home/john/";
      String fileName = "test.txt";

      InputStream inputStream;
      BufferedReader br;
      String line;

      sshConnection.createSshConnection(user, password, host, port);

      inputStream = sshConnection.download(remoteFilePath, fileName);

      br = new BufferedReader(new InputStreamReader(inputStream));
      while ((line = br.readLine()) != null)
        System.out.println(line);
      br.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws Exception {
    session.disconnect();
    channelSftp.disconnect();
  }
}