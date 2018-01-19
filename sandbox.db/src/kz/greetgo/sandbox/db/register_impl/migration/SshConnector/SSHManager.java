package kz.greetgo.sandbox.db.register_impl.migration.SshConnector;


import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationCia;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationFrs;
import kz.greetgo.util.RND;
import kz.greetgo.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Bean
public class SSHManager {

  private static int SSH_PORT;
  private static int CONNECTION_TIMEOUT;

  private static String HOSTNAME;
  private static String USERNAME;
  private static String PASSWORD;
  private static String SERVER_DIRECTORY;
  private static String LOCAL_DIRECTORY;

  private Session session;
  private Channel channel;
  private ChannelSftp channelSftp;

  MigrationCia cia = new MigrationCia();
  MigrationFrs frc = new MigrationFrs();

  public SSHManager() throws Exception {}

  private void initSession() throws Exception {
    JSch jsch = new JSch();

    readConfig();

    session = jsch.getSession(USERNAME, HOSTNAME, SSH_PORT);
    session.setPassword(PASSWORD);
    UserInfo userInfo = new SshUserInfo();

    session.setUserInfo(userInfo);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect(CONNECTION_TIMEOUT);
  }

  private void openSSH() throws Exception {
    initSession();
    channel = session.openChannel("sftp");
    channel.connect();

    channelSftp = (ChannelSftp) channel;
    channelSftp.cd(SERVER_DIRECTORY);
  }

  private void closeSSH() {
    channel.disconnect();
    session.disconnect();
  }

  public void connectAndMigrateCia(Connection connection) throws Exception {
    openSSH();

    List<String> filesForMigrate = new ArrayList<>();

    Pattern pattern = Pattern.compile("from_cia_[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]{6}.xml");
    Vector<ChannelSftp.LsEntry> xmlList = channelSftp.ls("*.xml");
    for (ChannelSftp.LsEntry entry : xmlList) {

      String fileName = entry.getFilename();
      Matcher m = pattern.matcher(fileName);
      if (m.matches()) {
        String tempFileName = fileName.replaceAll(".xml", ".XmlToMigrate");
        channelSftp.rename(fileName, tempFileName);
        filesForMigrate.add(tempFileName);
      }

    }

    closeSSH();
    migrateCia(filesForMigrate, connection);

  }

  private void migrateCia(List<String> filesForMigrate, Connection connection) throws Exception {

    Collections.sort(filesForMigrate);
    for (String fileName : filesForMigrate) {

      openSSH();

      String errorFileName = fileName + RND.intStr(3) + "_Error.log";

      File toMigrate = new File(LOCAL_DIRECTORY + "inFile_" + RND.intStr(10) + "_" + fileName);
      toMigrate.getParentFile().mkdirs();

      try (InputStream stream = channelSftp.get(fileName)) {

        try (FileOutputStream out = new FileOutputStream(toMigrate)) {
          ServerUtil.copyStreamsAndCloseIn(stream, out);
        }

      }

      closeSSH();

      cia.connection = connection;
      cia.inFile = toMigrate;
      cia.errorsFile = new File(LOCAL_DIRECTORY + errorFileName);
      cia.errorsFile.getParentFile().mkdirs();
      //
      cia.migrate();
      //

      openSSH();

      channelSftp.put(new FileInputStream(cia.errorsFile), cia.errorsFile.getName());
      channelSftp.rename(fileName,
        fileName.replaceAll(".XmlToMigrate", ".XmlMigrated"));

      closeSSH();

    }

  }

  public void connectAndMigrateFrs(Connection connection) throws Exception {

    List<String> filesForMigrate = new ArrayList<>();

    openSSH();

    Pattern pattern = Pattern.compile("from_frs_[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]{6}.json_row");
    Vector<ChannelSftp.LsEntry> xmlList = channelSftp.ls("*.json_row");
    for (ChannelSftp.LsEntry entry : xmlList) {

      String fileName = entry.getFilename();
      Matcher m = pattern.matcher(fileName);
      if (m.matches()) {
        String tempFileName = fileName.replaceAll(".json_row", ".JsonToMigrate");
        channelSftp.rename(fileName, tempFileName);
        filesForMigrate.add(tempFileName);
      }

    }
    closeSSH();

    migrateFrs(filesForMigrate, connection);

  }

  private void migrateFrs(List<String> filesForMigrate, Connection connection) throws Exception {

    Collections.sort(filesForMigrate);
    for (String fileName : filesForMigrate) {

      openSSH();

      String errorFileName = fileName + RND.intStr(3) + "_Error.log";

      File toMigrate = new File(LOCAL_DIRECTORY + "inFile_" + RND.intStr(10) + "_" + fileName);
      toMigrate.getParentFile().mkdirs();

      try (InputStream stream = channelSftp.get(fileName)) {

        try (FileOutputStream out = new FileOutputStream(toMigrate)) {
          ServerUtil.copyStreamsAndCloseIn(stream, out);
        }

      }

      closeSSH();

      frc.connection = connection;
      frc.inFile = toMigrate;
      frc.errorsFile = new File("build/migration/" + errorFileName);
      //
      frc.migrate();
      //

      openSSH();

      channelSftp.put(new FileInputStream(frc.errorsFile), frc.errorsFile.getName());
      channelSftp.rename(fileName,
        fileName.replaceAll(".JsonToMigrate", ".JsonMigrated"));

      closeSSH();

    }

  }

  public BeanGetter<SshConfig> sshConfig;


  private void readConfig() {

    SSH_PORT = sshConfig.get().port();
    CONNECTION_TIMEOUT = sshConfig.get().timeout();

    HOSTNAME = sshConfig.get().hostName();
    USERNAME = sshConfig.get().username();
    PASSWORD = sshConfig.get().pass();
    SERVER_DIRECTORY = sshConfig.get().serverDir();
    LOCAL_DIRECTORY = sshConfig.get().localDir();

  }


  public void renameToDefault() throws Exception {

    openSSH();

    Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.XmlMigrated");

    for (ChannelSftp.LsEntry entry : list) {

      String fileName = entry.getFilename();

      String tempFileName = fileName.replaceAll(".XmlMigrated", ".xml");

      channelSftp.rename(fileName, tempFileName);

    }

    Vector<ChannelSftp.LsEntry> list2 = channelSftp.ls("*.JsonMigrated");

    for (ChannelSftp.LsEntry entry : list2) {

      String fileName = entry.getFilename();

      String tempFileName = fileName.replaceAll(".JsonMigrated", ".json_row");

      channelSftp.rename(fileName, tempFileName);

    }

    closeSSH();

  }

}
